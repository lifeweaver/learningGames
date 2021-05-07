package net.stardecimal.game.missilecommand

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ai.steer.SteeringBehavior
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.maps.MapLayers
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.RandomXS128
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.utils.Array
import net.stardecimal.game.BodyFactory
import net.stardecimal.game.DFUtils
import net.stardecimal.game.DefaultLevelFactory
import net.stardecimal.game.ParticleEffectManager
import net.stardecimal.game.ai.SteeringPresets
import net.stardecimal.game.entity.components.BulletComponent
import net.stardecimal.game.entity.components.CollisionComponent
import net.stardecimal.game.entity.components.Mapper
import net.stardecimal.game.entity.components.ParticleEffectComponent
import net.stardecimal.game.entity.components.PlayerComponent
import net.stardecimal.game.entity.components.SdBodyComponent
import net.stardecimal.game.entity.components.SdLocation
import net.stardecimal.game.entity.components.SoundEffectComponent
import net.stardecimal.game.entity.components.StateComponent
import net.stardecimal.game.entity.components.SteeringComponent
import net.stardecimal.game.entity.components.TextureComponent
import net.stardecimal.game.entity.components.TransformComponent
import net.stardecimal.game.entity.components.TypeComponent
import net.stardecimal.game.entity.systems.RenderingSystem
import net.stardecimal.game.loader.SdAssetManager
import net.stardecimal.game.missilecommand.entity.components.EnemyComponent
import net.stardecimal.game.missilecommand.entity.systems.EnemySpawningSystem

class LevelFactory implements DefaultLevelFactory {
	private Texture cityTex, defenderMissileTex, explosionTex, bomberPlaneTex, satelliteTex, smartBombTex, crosshairsTex
	private TextureRegion cellBackground, cellBackgroundRed, missileTex, nothingTex
	Sound boom, targeting_beep
	private float defenderMissileWidth = 0.5
	private float defenderMissileHeight = 0.75
	RandomXS128 rand = new RandomXS128()
	def defenderMissiles
	final short enemyGroup = -1

	//TODO:
	//add scoring

	LevelFactory(PooledEngine en, SdAssetManager assetManager) {
		init(en, assetManager)

		//Specific textures
		cityTex  = assetManager.manager.get(SdAssetManager.city)
		defenderMissileTex = assetManager.manager.get(SdAssetManager.defenderMissile)
		explosionTex = assetManager.manager.get(SdAssetManager.explosion)
		bomberPlaneTex = assetManager.manager.get(SdAssetManager.bomberPlane)
		satelliteTex = assetManager.manager.get(SdAssetManager.satellite)
		smartBombTex = assetManager.manager.get(SdAssetManager.smartBomb)
		crosshairsTex = assetManager.manager.get(SdAssetManager.crosshairs)
		cellBackground =  DFUtils.makeTextureRegion(2, 2, '#000000')
		cellBackgroundRed =  DFUtils.makeTextureRegion(2, 2, '#ff0000')
		missileTex = DFUtils.makeTextureRegion(1, 1, '#ffffff')
		nothingTex = DFUtils.makeTextureRegion(0,0,'#000000')
		boom = assetManager.manager.get(SdAssetManager.boom)
		targeting_beep = assetManager.manager.get(SdAssetManager.targeting_beep)
		pem.addParticleEffect(ParticleEffectManager.CONTRAIL, assetManager.manager.get(SdAssetManager.enemyMissileTrail))
		pem.addParticleEffect(ParticleEffectManager.EXPLOSION, assetManager.manager.get(SdAssetManager.explosionParticle))
		defenderMissiles = new ArrayList<Entity>()

		log.info("level factory initialized")
	}

	@Override
	TiledMap generateBackground() {
		Vector2 screenSize = RenderingSystem.getScreenSizeInMeters()
		screenSize.x = screenSize.x / RenderingSystem.PPM
		screenSize.y = screenSize.y / RenderingSystem.PPM
		TiledMap map = new TiledMap()
		MapLayers layers = map.layers
		TiledMapTileLayer layer1 = new TiledMapTileLayer(screenSize.x as int, screenSize.y as int, 1, 1)

		for (int row = 0; row < screenSize.y; row++) {
			for (int col = 0; col < screenSize.x; col++) {
				TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell()

				switch(row) {
					case 0..1:
						cell.setTile(new StaticTiledMapTile(cellBackgroundRed))
						break

					case 2:
						if(col in 0..5 || col in 17..22 || col in 34..39) {
							cell.setTile(new StaticTiledMapTile(cellBackgroundRed))
						} else {
							cell.setTile(new StaticTiledMapTile(cellBackground))
						}
						break

					case 3:
						if(col in 1..4 || col in 18..21 || col in 35..38) {
							cell.setTile(new StaticTiledMapTile(cellBackgroundRed))
						} else {
							cell.setTile(new StaticTiledMapTile(cellBackground))
						}
						break
				}

				layer1.setCell(col, row, cell)
			}
		}

		layers.add(layer1)

		return map
	}

	void createCities() {
		[
				new Vector2(7, 3),
				new Vector2(11, 3.5),
				new Vector2(15, 3),
				new Vector2(25, 3),
				new Vector2(29, 3.5),
				new Vector2(33, 3)
		].each {
			createCity(it)
		}
	}

	Entity createCity(Vector2 start) {
		Entity entity = engine.createEntity()
		SdBodyComponent sdBody = engine.createComponent(SdBodyComponent)
		TextureComponent texture = engine.createComponent(TextureComponent)
		TypeComponent type = engine.createComponent(TypeComponent)
		TransformComponent position = engine.createComponent(TransformComponent)

		sdBody.width = 3
		sdBody.height = 1.5
		sdBody.body = bodyFactory.makeBoxPolyBody(
				start.x,
				start.y,
				sdBody.width,
				sdBody.height,
				BodyFactory.STONE,
				BodyDef.BodyType.StaticBody,
				true
		)
		sdBody.body.setUserData(entity)

		texture.region = new TextureRegion(cityTex)
		type.type = TypeComponent.TYPES.CITY

		entity.add(sdBody)
		entity.add(texture)
		entity.add(position)
		entity.add(type)
		engine.addEntity(entity)

		return entity
	}

	void createGroundBarrier() {
		Entity entity = engine.createEntity()
		Vector2 screenSize = RenderingSystem.getScreenSizeInMeters()
		TypeComponent type = engine.createComponent(TypeComponent)
		SdBodyComponent sdBody = engine.createComponent(SdBodyComponent)

		sdBody.width = screenSize.x / RenderingSystem.PPM
		sdBody.height = 0.1
		sdBody.body = bodyFactory.makeEdgeBody(
				0,
				1,
				sdBody.width,
				1,
				BodyDef.BodyType.StaticBody
		)
		sdBody.body.setUserData(entity)
		type.type = TypeComponent.TYPES.SCENERY

		entity.add(sdBody)
		entity.add(type)
		engine.addEntity(entity)
	}

	Entity createPlayer(OrthographicCamera cam) {
		Entity entity = engine.createEntity()
		TypeComponent type = engine.createComponent(TypeComponent)
		PlayerComponent player = engine.createComponent(PlayerComponent)

		player.cam = cam
		type.type = TypeComponent.TYPES.PLAYER

		entity.add(player)
		engine.addEntity(entity)

		return entity
	}

	void createDefenderMissiles() {
		[
				defenderMissileGroup(1.5, 2.5),
				defenderMissileGroup(18.5, 2.5),
				defenderMissileGroup(35.5, 2.5)
		].each {
			it.each {Vector2 missilePos ->
				createDefenderMissile(missilePos)
			}
		}
	}

	static List<Vector2> defenderMissileGroup(float x, float y, int rows = 4) {
		List<Vector2> missiles = []
		float adjustment = 0

		(0..rows - 1).reverse().each {
			if(it != 0) {
				(0..it).each {
					missiles << new Vector2(adjustment + it + x as float, y + adjustment as float)
				}
			} else {
				missiles << new Vector2(adjustment + x as float, y + adjustment as float)
			}
			adjustment += 0.5
		}

		return missiles
	}

	Entity createDefenderMissile(Vector2 start) {
		Entity entity = engine.createEntity()
		SdBodyComponent sdBody = engine.createComponent(SdBodyComponent)
		TextureComponent texture = engine.createComponent(TextureComponent)
		TypeComponent type = engine.createComponent(TypeComponent)
		TransformComponent position = engine.createComponent(TransformComponent)

		sdBody.width = defenderMissileWidth
		sdBody.height = defenderMissileHeight
		sdBody.body = bodyFactory.makeBoxPolyBody(
				start.x,
				start.y,
				sdBody.width,
				sdBody.height,
				BodyFactory.STONE,
				BodyDef.BodyType.KinematicBody,
				false
		)
		sdBody.body.setUserData(entity)

		texture.region = new TextureRegion(defenderMissileTex)
		type.type = TypeComponent.TYPES.DEFENDER_MISSILE

		entity.add(sdBody)
		entity.add(texture)
		entity.add(position)
		entity.add(type)
		engine.addEntity(entity)
		defenderMissiles.add(entity)

		return entity
	}

	void launchDefenderMissile(float x, float y) {
		Vector2 target = new Vector2(x, y)

		Entity closestMissile = null
		float closestDistance = 0

		//Determine closest missile
		defenderMissiles.each {
			SdBodyComponent bCom = Mapper.bCom.get(it)
			//Make sure it's not dead, and hasn't already launched(has bullet comp)
			if(bCom && !bCom.isDead && !Mapper.bulletCom.get(it)) {
				if(closestMissile) {
					Vector3 pos3 = Mapper.transCom.get(it)?.position
					if(!pos3) {
						return
					}
					Vector2 pos = new Vector2(pos3.x, pos3.y)

					float newDst = target.dst(pos)
					if(newDst < closestDistance) {
						closestMissile = it
						closestDistance = newDst
					}
				} else {
					closestMissile = it
					Vector3 pos3 = Mapper.transCom.get(closestMissile)?.position
					if(!pos3) {
						return
					}
					Vector2 pos = new Vector2(pos3.x, pos3.y)
					closestDistance = target.dst(pos)
				}
			}
		}

		if(!closestMissile) {
			log.debug("All missiles used")
			return
		}
		defenderMissiles.remove(closestMissile)

		//Launch missile toward point
		BulletComponent bul = engine.createComponent(BulletComponent)
		SdBodyComponent sdBody = Mapper.bCom.get(closestMissile)

		// Get angle to target
		Vector3 pos3 = Mapper.transCom.get(closestMissile).position
		Vector2 pos = new Vector2(pos3.x, pos3.y)
		float targetAngle = MathUtils.atan2(pos.y - target.y as float, target.x - pos.x as float)

		//rotate missile to point at target
		sdBody.body.setTransform(sdBody.body.position, DFUtils.vectorToAngle(DFUtils.aimTo(pos, target)))
		sdBody.body.bullet = true

		//Add velocity
		bul.xVel = 15 * MathUtils.cos(targetAngle) as float
		bul.yVel = 15 * MathUtils.sin(targetAngle) * -1 as float

		bul.startPos = pos
		bul.maxDist = bul.startPos.dst(target)
		bul.owner = BulletComponent.Owner.PLAYER
		bul.particleEffect = makeParticleEffect(ParticleEffectManager.CONTRAIL, sdBody, 0, 0, true)
//		log.debug("targetAngle: ${targetAngle * MathUtils.radiansToDegrees}, bul.xVel: ${bul.xVel}, bul.yVel: ${bul.yVel}, maxDist: ${bul.maxDist}, startPos: ${bul.startPos}, target: ${target}, test: ${distance(bul.startPos, target)}")

		closestMissile.add(bul)
	}

	Entity createBoom(Vector2 boomCenter) {
		log.debug("boom at: ${boomCenter}")
		Entity entity = engine.createEntity()
		SdBodyComponent sdBody = engine.createComponent(SdBodyComponent)
		TypeComponent type = engine.createComponent(TypeComponent)
		ParticleEffectComponent pec = engine.createComponent(ParticleEffectComponent)
		CollisionComponent colComp = engine.createComponent(CollisionComponent)

		float explosionRange = 3
		sdBody.width = explosionRange
		sdBody.height = explosionRange
		sdBody.body = bodyFactory.makeCirclePolyBody(
				boomCenter.x,
				boomCenter.y,
				sdBody.width,
				BodyFactory.NOTHING,
				BodyDef.BodyType.KinematicBody,
				false,
				true
		)
		sdBody.body.setUserData(entity)
		type.type = TypeComponent.TYPES.EXPLOSION

		pec.particleEffect = pem.getPooledParticleEffect(ParticleEffectManager.EXPLOSION)
		pec.particleEffect.setPosition(sdBody.body.position.x, sdBody.body.position.y)
		pec.particleEffect.scaleEffect(explosionRange, 1)
		pec.isAttached = true
		pec.attachedBody = sdBody.body

		entity.add(colComp)
		entity.add(sdBody)
		entity.add(pec)
		entity.add(type)
		engine.addEntity(entity)

		//RayCast method to catch everything on the edges
		Array<Body> entitiesHitByExplosion = circleRayCast(boomCenter, explosionRange / 2 as float)
		entitiesHitByExplosion.each {
			if(it?.userData instanceof Entity) {
				Entity ent = it.userData as Entity
				Mapper.bCom.get(ent).isDead = true
			}
		}

		//AABB method to catch ones the RayCast missed. Like completely overlapping?
		float radius = explosionRange / 2 as float
		Vector2 lower = new Vector2(boomCenter)
		Vector2 upper = new Vector2(boomCenter)

		lower.sub(radius, radius)
		upper.add(radius, radius)

		def entitiesInTheExplosion = aabb(lower, upper)
		entitiesInTheExplosion.each {
			if(it?.userData instanceof Entity) {
				Entity ent = it.userData as Entity
				Mapper.bCom.get(ent).isDead = true
			}
		}

		boom.play()
		return entity
	}

	void startMissileBarrage() {
		engine.getSystem(EnemySpawningSystem).processing = true
	}

	static float[] getMinAndMaxAngles(Vector2 screenSize, Vector2 startPos) {
		float maxX = screenSize.x / RenderingSystem.PPM
		Vector2 minXY = new Vector2(0, 0)
		Vector2 maxXY = new Vector2(maxX, 0)

//		Triangle
		float sideA = startPos.dst(minXY)
		float sideB = startPos.dst(maxXY)
		float sideC = maxX

//		float angleA = MathUtils.acos((sideB * sideB + sideC * sideC - sideA * sideA) / (2 * sideB * sideC) as float) * MathUtils.radiansToDegrees as float
//		float angleB = MathUtils.acos((sideA * sideA + sideC * sideC - sideB * sideB) / (2 * sideA * sideC) as float) * MathUtils.radiansToDegrees as float
		float angleC = MathUtils.acos((sideA * sideA + sideB * sideB - sideC * sideC) / (2 * sideA * sideB) as float) * MathUtils.radiansToDegrees as float
//		log.debug("jtest - sideA: ${sideA}, sideB: ${sideB}, sideC: ${sideC}")
//		log.debug("jtest2 - angleA: ${angleA}, angleB: ${angleB}, angleC: ${angleC}")

		float angle = determineRightAngle(screenSize, startPos)
		float maxAngle = angleC + angle as float

		return [angle, maxAngle]
	}

	static float determineRightAngle(Vector2 screenSize, Vector2 startPos) {
		float maxX = screenSize.x / RenderingSystem.PPM
		float maxY = screenSize.y / RenderingSystem.PPM
		Vector2 maxXY = new Vector2(maxX, maxY)
		Vector2 bottom = new Vector2(maxX, 0)

		float sideA = maxXY.dst(bottom)
		float sideB = startPos.dst(bottom)
		float sideC = startPos.dst(maxXY)

		float angleA = MathUtils.acos((sideB * sideB + sideC * sideC - sideA * sideA) / (2 * sideB * sideC) as float) * MathUtils.radiansToDegrees as float
//		float angleB = MathUtils.acos((sideA * sideA + sideC * sideC - sideB * sideB) / (2 * sideA * sideC) as float) * MathUtils.radiansToDegrees as float
//		float angleC = MathUtils.acos((sideA * sideA + sideB * sideB - sideC * sideC) / (2 * sideA * sideB) as float) * MathUtils.radiansToDegrees as float

//		log.debug("jtest3 - sideA: ${sideA}, sideB: ${sideB}, sideC: ${sideC}")
//		log.debug("jtest4 - angleA: ${angleA}, angleB: ${angleB}, angleC: ${angleC}")

		return angleA
	}

	// If spawnPoint is passed in, that is where the missile should spawn
	// Used by the bomber plane and the satellite, and maybe the missile splitting
	Entity createEnemyMissile(Vector2 startPosOverride=null, float angleOverride=0) {
		Entity entity = engine.createEntity()
		SdBodyComponent sdBody = engine.createComponent(SdBodyComponent)
		TransformComponent position = engine.createComponent(TransformComponent)
		TextureComponent texture = engine.createComponent(TextureComponent)
		TypeComponent type = engine.createComponent(TypeComponent)
		CollisionComponent colComp = engine.createComponent(CollisionComponent)
		BulletComponent bul = engine.createComponent(BulletComponent)
		Vector2 screenSize = RenderingSystem.getScreenSizeInMeters()
		EnemyComponent ecom = engine.createComponent(EnemyComponent)

		float randX, y
		if(startPosOverride) {
			randX = startPosOverride.x
			y = startPosOverride.y
		} else {
			randX = rand.nextInt(screenSize.x / RenderingSystem.PPM as int)
			y = screenSize.y / RenderingSystem.PPM as float
		}

		sdBody.body = bodyFactory.makeCirclePolyBody(randX, y,0.2f, BodyFactory.PING_PONG, BodyDef.BodyType.DynamicBody,true)
		sdBody.body.fixtureList.first().filterData.groupIndex = enemyGroup
		sdBody.body.setBullet(true) // increase physics computation to limit body travelling through other objects
		position.position.set(sdBody.body.position.x, sdBody.body.position.y,0)

		float randAngle = angleOverride
		if(!angleOverride) {
			float[] angles = getMinAndMaxAngles(screenSize, sdBody.body.position)
			float minAngle = angles[0]
			float maxAngle = angles[1]
			randAngle = rand.nextInt(maxAngle - minAngle as int) + minAngle as float
//			log.debug("start: ${sdBody.body.position}, randAngle: ${randAngle}, minAngle: ${minAngle}, maxAngle: ${maxAngle}")
		}

		texture.region = missileTex
		type.type = TypeComponent.TYPES.BULLET
		sdBody.body.setUserData(entity)

		//Add velocity
		bul.xVel = 2.5 * MathUtils.cos(MathUtils.degreesToRadians * randAngle as float) as float
		bul.yVel = 2.5 * MathUtils.sin(MathUtils.degreesToRadians * randAngle as float) * -1 as float
		bul.owner = BulletComponent.Owner.ENEMY
		bul.particleEffect = makeParticleEffect(ParticleEffectManager.CONTRAIL, sdBody, 0, 0, true)

		entity.add(bul)
		entity.add(colComp)
		entity.add(sdBody)
		entity.add(position)
		entity.add(texture)
		entity.add(type)
		entity.add(ecom)

		engine.addEntity(entity)

		return entity
	}

	void createBomberPlane() {
		Entity entity = engine.createEntity()
		SdBodyComponent sdBody = engine.createComponent(SdBodyComponent)
		TransformComponent position = engine.createComponent(TransformComponent)
		TextureComponent texture = engine.createComponent(TextureComponent)
		TypeComponent type = engine.createComponent(TypeComponent)
		EnemyComponent ecom = engine.createComponent(EnemyComponent)
		Vector2 screenSize = RenderingSystem.getScreenSizeInMeters()
		float maxX = screenSize.x / RenderingSystem.PPM as float

		float randX = rand.nextInt(maxX as int) > 20 ? maxX : 0
		float randY = rand.nextInt(10) + 12

		sdBody.width = 3
		sdBody.height = 1.5
		sdBody.body = bodyFactory.makeBoxPolyBody(
				randX,
				randY,
				sdBody.width,
				sdBody.height,
				BodyFactory.STONE,
				BodyDef.BodyType.KinematicBody
		)
		sdBody.body.fixtureList.first().filterData.groupIndex = enemyGroup
		sdBody.body.setUserData(entity)

		//Starting point specific settings
		if(randX == 0) {
			texture.region = new TextureRegion(bomberPlaneTex)
			position.flipX = true

			//Velocity
			sdBody.body.setLinearVelocity(MathUtils.lerp(sdBody.body.linearVelocity.x, 5, 1f), 0)
		} else {
			texture.region = new TextureRegion(bomberPlaneTex)

			//Velocity
			sdBody.body.setLinearVelocity(MathUtils.lerp(sdBody.body.linearVelocity.x, -5, 1f), 0)
		}

		type.type = TypeComponent.TYPES.BOMBER_PLANE

		entity.add(sdBody)
		entity.add(position)
		entity.add(texture)
		entity.add(type)
		entity.add(ecom)

		engine.addEntity(entity)
	}

	void createSatellite() {
		Entity entity = engine.createEntity()
		SdBodyComponent sdBody = engine.createComponent(SdBodyComponent)
		TransformComponent position = engine.createComponent(TransformComponent)
		TextureComponent texture = engine.createComponent(TextureComponent)
		TypeComponent type = engine.createComponent(TypeComponent)
		EnemyComponent ecom = engine.createComponent(EnemyComponent)
		Vector2 screenSize = RenderingSystem.getScreenSizeInMeters()
		float maxX = screenSize.x / RenderingSystem.PPM as float

		float randX = rand.nextInt(maxX as int) > 20 ? maxX : 0
		float randY = rand.nextInt(5) + 25

		sdBody.width = 2
		sdBody.height = 2
		sdBody.body = bodyFactory.makeBoxPolyBody(
				randX,
				randY,
				sdBody.width,
				sdBody.height,
				BodyFactory.STONE,
				BodyDef.BodyType.KinematicBody
		)
		sdBody.body.fixtureList.first().filterData.groupIndex = enemyGroup
		sdBody.body.setUserData(entity)

		//Starting point specific settings
		if(randX == 0) {
			texture.region = new TextureRegion(satelliteTex)
			position.flipX = true

			//Velocity
			sdBody.body.setLinearVelocity(MathUtils.lerp(sdBody.body.linearVelocity.x, 7, 1f), 0)
		} else {
			texture.region = new TextureRegion(satelliteTex)

			//Velocity
			sdBody.body.setLinearVelocity(MathUtils.lerp(sdBody.body.linearVelocity.x, -7, 1f), 0)
		}

		type.type = TypeComponent.TYPES.SATELLITE

		entity.add(sdBody)
		entity.add(position)
		entity.add(texture)
		entity.add(type)
		entity.add(ecom)

		engine.addEntity(entity)
	}

	void createSmartBomb(Entity target) {
		Entity entity = engine.createEntity()
		SdBodyComponent sdBody = engine.createComponent(SdBodyComponent)
		TransformComponent position = engine.createComponent(TransformComponent)
		TextureComponent texture = engine.createComponent(TextureComponent)
		TypeComponent type = engine.createComponent(TypeComponent)
		EnemyComponent ecom = engine.createComponent(EnemyComponent)
		CollisionComponent colComp = engine.createComponent(CollisionComponent)
		SteeringComponent scom = engine.createComponent(SteeringComponent)
		StateComponent stateCom = engine.createComponent(StateComponent)
		SoundEffectComponent soundCom = engine.createComponent(SoundEffectComponent)

		Vector2 screenSize = RenderingSystem.getScreenSizeInMeters()
		double maxX = screenSize.x / RenderingSystem.PPM
		float randX = rand.nextInt(maxX as int)

		sdBody.width = 0.5f
		sdBody.height = 0.5f
		sdBody.body = bodyFactory.makeBoxPolyBody(
				randX,
				screenSize.y / RenderingSystem.PPM as float,
				sdBody.width,
				sdBody.height,
				BodyFactory.STONE,
				BodyDef.BodyType.DynamicBody
		)
		sdBody.body.fixtureList.first().filterData.groupIndex = enemyGroup
		sdBody.body.setUserData(entity)
		texture.region = new TextureRegion(smartBombTex)
		type.type = TypeComponent.TYPES.SMART_BOMB

		stateCom.state = StateComponent.STATE_NORMAL
		ecom.target = target
		scom.body = sdBody.body
		SteeringBehavior<Vector2> steeringBehavior = SteeringPresets.getArrive(scom, new SdLocation(position: Mapper.bCom.get(target).body.position, orientation: 0))
		scom.maxLinearSpeed = 10f
		scom.steeringBehavior = steeringBehavior

		soundCom.soundEffect = targeting_beep
		soundCom.looping = true
		soundCom.play()

		entity.add(soundCom)
		entity.add(colComp)
		entity.add(sdBody)
		entity.add(position)
		entity.add(texture)
		entity.add(type)
		entity.add(ecom)
		entity.add(scom)
		entity.add(stateCom)

		engine.addEntity(entity)
	}

	void createCrosshair() {
		Gdx.graphics.setCursor(DFUtils.textureRegionToCursor(new TextureRegion(crosshairsTex), crosshairsTex.width / 2 as int, crosshairsTex.height / 2 as int))
	}

	List<Entity> findTargets() {
		List<Entity> targets = engine.getEntities().findAll {
			Mapper.typeCom.get(it)?.type == TypeComponent.TYPES.CITY || Mapper.typeCom.get(it)?.type == TypeComponent.TYPES.DEFENDER_MISSILE
		}
		Collections.shuffle(targets)

		return (List<Entity>) targets
	}

	Entity launchEnemyMissile(Vector2 startPos) {
		List<Entity> targets = findTargets()

		if(targets.size()) {
			Entity target = targets.first() as Entity
			Vector2 missileStart = new Vector2(startPos.x, startPos.y)
			Vector2 targetPos = Mapper.bCom.get(target).body.position
			Vector2 aimedVector = DFUtils.aimTo(missileStart, targetPos)
			float angleDeg = DFUtils.vectorToAngle2(aimedVector) * MathUtils.radiansToDegrees as float
//						    println("missileStart: ${missileStart}, target: ${targetPos}, angle: ${angleDeg}")

			return createEnemyMissile(missileStart, angleDeg)
		}

		return null
	}

	void initScore() {
		Vector2 screenSize = RenderingSystem.getScreenSizeInMeters()
//		screenSize.x / RenderingSystem.PPM / 2 as float, 2f
		//TODO: Add fontQueue to rendering system?
//		BitmapFont font = new BitmapFont()
//		font.draw(null, "Score: 0", screenSize.x / RenderingSystem.PPM / 2 as float, screenSize.y / RenderingSystem.PPM - 2 as float)
	}

}
