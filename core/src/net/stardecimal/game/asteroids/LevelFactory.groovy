package net.stardecimal.game.asteroids

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.ai.steer.SteeringBehavior
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.math.RandomXS128
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import net.stardecimal.game.BodyFactory
import net.stardecimal.game.DFUtils
import net.stardecimal.game.DefaultHud
import net.stardecimal.game.DefaultLevelFactory
import net.stardecimal.game.ParticleEffectManager
import net.stardecimal.game.ai.SteeringPresets
import net.stardecimal.game.asteroids.entity.systems.PlayerControlSystem
import net.stardecimal.game.entity.components.BulletComponent
import net.stardecimal.game.entity.components.CollisionComponent
import net.stardecimal.game.entity.components.EnemyComponent
import net.stardecimal.game.entity.components.Mapper
import net.stardecimal.game.entity.components.ParticleEffectComponent
import net.stardecimal.game.entity.components.PlayerComponent
import net.stardecimal.game.entity.components.ScoreComponent
import net.stardecimal.game.entity.components.SdBodyComponent
import net.stardecimal.game.entity.components.SdLocation
import net.stardecimal.game.entity.components.SoundEffectComponent
import net.stardecimal.game.entity.components.StateComponent
import net.stardecimal.game.entity.components.SteeringComponent
import net.stardecimal.game.entity.components.TextureComponent
import net.stardecimal.game.entity.components.TransformComponent
import net.stardecimal.game.entity.components.TypeComponent
import net.stardecimal.game.entity.components.VelocityComponent
import net.stardecimal.game.entity.systems.RenderingSystem
import net.stardecimal.game.loader.SdAssetManager

class LevelFactory implements DefaultLevelFactory {
	private TextureRegion enemyTex, playerTex, shotTex, asteroidTex
	Sound enemyBlownUp, playerBlownUp, playerFiring, background
	RandomXS128 rand = new RandomXS128()
	Entity player

	LevelFactory(PooledEngine en, SdAssetManager assetManager) {
		init(en, assetManager)

		//Specific textures
		TextureAtlas atlas = assetManager.manager.get(SdAssetManager.gameImages)
		atlas.findRegion("asteroids/")

		playerTex = atlas.findRegion("asteroids/player")
		enemyTex = atlas.findRegion("asteroids/enemy")
		asteroidTex = atlas.findRegion("asteroids/asteroid")
		shotTex = DFUtils.makeTextureRegion(0.25, 0.25, '#FFFFFF')

		enemyBlownUp = assetManager.manager.get(SdAssetManager.enemyBlownUp)
		playerBlownUp = assetManager.manager.get(SdAssetManager.playerBlownUp)
		playerFiring = assetManager.manager.get(SdAssetManager.playerFiring)
		pem.addParticleEffect(ParticleEffectManager.FLAMES , assetManager.manager.get(SdAssetManager.flames))

		log.info("level factory initialized")
	}

	//TODO:
	//Make enemy shoot
	//High score list and initials etc. JMD
	//Blow up animation?
	//hyperspace or shield


	void createPlayer(OrthographicCamera cam) {
		//Reset controls on death
		controller.reset()

		//Reset the angle of the ship on death
		engine.getSystem(PlayerControlSystem).radians = 0

		Entity entity = engine.createEntity()
		SdBodyComponent sdBody = engine.createComponent(SdBodyComponent)
		TransformComponent position = engine.createComponent(TransformComponent)
		TextureComponent texture = engine.createComponent(TextureComponent)
		PlayerComponent playerCom = engine.createComponent(PlayerComponent)
		TypeComponent type = engine.createComponent(TypeComponent)
		VelocityComponent velCom = engine.createComponent(VelocityComponent)
		ParticleEffectComponent pec = engine.createComponent(ParticleEffectComponent)
		Vector2 screenSize = RenderingSystem.getScreenSizeInMeters()

		playerCom.cam = cam
		sdBody.body = bodyFactory.makeBoxPolyBody(
				screenSize.x / RenderingSystem.PPM / 2 as float,
				screenSize.y / RenderingSystem.PPM / 2 as float,
				1,
				1,
				BodyFactory.STEEL,
				BodyDef.BodyType.DynamicBody,
				false
		)

		texture.region = playerTex
		type.type = TypeComponent.TYPES.PLAYER
		sdBody.invulnerabilityTime = 2
		sdBody.body.setUserData(entity)
		velCom.removeAfterProcessing = false

		pec.particleEffect = pem.getPooledParticleEffect(ParticleEffectManager.FLAMES)
		pec.particleEffect.setPosition(sdBody.body.position.x, sdBody.body.position.y)
		pec.particleEffect.scaleEffect(0.01, 0.1)
		pec.angleEmitters = true
		pec.isAttached = true
		pec.attachedBody = sdBody.body
		pec.killOnParentBodyDeath = true
		pec.timeTilDeath = 1000000000000

		entity.add(pec)
		entity.add(velCom)
		entity.add(sdBody)
		entity.add(position)
		entity.add(texture)
		entity.add(playerCom)
		entity.add(type)
		engine.addEntity(entity)
		player = entity
	}

	void createEnemy() {
		Entity entity = engine.createEntity()
		SdBodyComponent sdBody = engine.createComponent(SdBodyComponent)
		TransformComponent position = engine.createComponent(TransformComponent)
		TextureComponent texture = engine.createComponent(TextureComponent)
		EnemyComponent eCom = engine.createComponent(EnemyComponent)
		TypeComponent type = engine.createComponent(TypeComponent)
		SteeringComponent scom = engine.createComponent(SteeringComponent)
		StateComponent stateCom = engine.createComponent(StateComponent)
		Vector2 screenSize = RenderingSystem.getScreenSizeInMeters()
		float randX = rand.nextInt(1) ? 0 : screenSize.x / RenderingSystem.PPM
		float randY = rand.nextInt(screenSize.y / RenderingSystem.PPM as int)

		sdBody.body = bodyFactory.makeBoxPolyBody(
				randX,
				randY,
				1.5,
				1,
				BodyFactory.STEEL,
				BodyDef.BodyType.DynamicBody,
				true
		)

		texture.region = enemyTex
		type.type = TypeComponent.TYPES.ENEMY
		sdBody.body.setUserData(entity)

		stateCom.state = StateComponent.STATE_NORMAL
		eCom.target = player
		scom.body = sdBody.body
		SteeringBehavior<Vector2> steeringBehavior = SteeringPresets.getArrive(scom, new SdLocation(position: Mapper.bCom.get(player).body.position, orientation: 0))
		scom.maxLinearSpeed = 5f
		scom.steeringBehavior = steeringBehavior

		entity.add(stateCom)
		entity.add(scom)
		entity.add(sdBody)
		entity.add(position)
		entity.add(texture)
		entity.add(eCom)
		entity.add(type)
		engine.addEntity(entity)
	}

	void playerShoot() {
		Body body = Mapper.bCom.get(player).body
		createShot(body.position, body.angle)
	}

	void createShot(Vector2 startPos, float angle) {
		Entity entity = engine.createEntity()
		SdBodyComponent sdBody = engine.createComponent(SdBodyComponent)
		TransformComponent position = engine.createComponent(TransformComponent)
		TextureComponent texture = engine.createComponent(TextureComponent)
		CollisionComponent colComp = engine.createComponent(CollisionComponent)
		TypeComponent type = engine.createComponent(TypeComponent)
		BulletComponent bul = engine.createComponent(BulletComponent)
		VelocityComponent velCom = engine.createComponent(VelocityComponent)

		DFUtils.angleToVector(velCom.linearVelocity, angle)
		Vector2 shotStartPos = new Vector2(velCom.linearVelocity.x, velCom.linearVelocity.y).add(startPos)
		velCom.linearVelocity.x += velCom.linearVelocity.x * 15
		velCom.linearVelocity.y += velCom.linearVelocity.y * 15

		sdBody.body = bodyFactory.makeBoxPolyBody(
				shotStartPos.x,
				shotStartPos.y,
				0.25,
				0.25,
				BodyFactory.STONE,
				BodyDef.BodyType.DynamicBody,
				true
		)

		type.type = TypeComponent.TYPES.BULLET
		sdBody.body.bullet = true
		sdBody.body.setUserData(entity)
		texture.region = shotTex


		bul.owner = BulletComponent.Owner.PLAYER
		bul.maxLife = 5

		SoundEffectComponent soundCom = engine.createComponent(SoundEffectComponent)
		soundCom.soundEffect = playerFiring
		soundCom.play()
		entity.add(soundCom)


		entity.add(bul)
		entity.add(velCom)
		entity.add(sdBody)
		entity.add(position)
		entity.add(texture)
		entity.add(colComp)
		entity.add(type)
		engine.addEntity(entity)
	}

	void createAsteroid(Vector2 startPos=null, Vector2 velocity=null, int asteroidType=TypeComponent.TYPES.ASTEROID) {
		Entity entity = engine.createEntity()
		SdBodyComponent sdBody = engine.createComponent(SdBodyComponent)
		TransformComponent position = engine.createComponent(TransformComponent)
		TextureComponent texture = engine.createComponent(TextureComponent)
		TypeComponent type = engine.createComponent(TypeComponent)
		VelocityComponent velCom = engine.createComponent(VelocityComponent)
		ScoreComponent scoreCom = engine.createComponent(ScoreComponent)
		CollisionComponent colComp = engine.createComponent(CollisionComponent)
		Vector2 screenSize = RenderingSystem.getScreenSizeInMeters()

		float maxX = screenSize.x / RenderingSystem.PPM as float
		float maxY = screenSize.y / RenderingSystem.PPM as float
		float randX = rand.nextInt(maxX as int)
		float randY = rand.nextInt(maxY as int)

		//Don't spawn on the player
		if(player) {
			Vector3 playerPos = player ? Mapper.transCom.get(player).position : new Vector3(screenSize.x / RenderingSystem.PPM / 2 as float, screenSize.y / RenderingSystem.PPM / 2 as float, 0)
			if(Math.abs(playerPos.x - randX) < 3 || Math.abs(playerPos.y - randY) < 3) {
				randX += 4
				randY += 4
			}
		}

		type.type = asteroidType

		if(asteroidType == TypeComponent.TYPES.ASTEROID) {
			scoreCom.worth = 50
			position.scale.x = 0.8
			position.scale.y = 0.8
			sdBody.body = bodyFactory.makeCirclePolyBody(
					randX,
					randY,
					2,
					BodyFactory.STONE,
					BodyDef.BodyType.DynamicBody
			)
			velCom.linearVelocity.x = rand.nextInt(5)
			velCom.linearVelocity.y = rand.nextInt(5)
		} else if(asteroidType == TypeComponent.TYPES.MEDIUM_ASTEROID) {
			scoreCom.worth = 100
			position.scale.x = 0.7
			position.scale.y = 0.7
			sdBody.body = bodyFactory.makeCirclePolyBody(
					startPos.x,
					startPos.y,
					1.25,
					BodyFactory.STONE,
					BodyDef.BodyType.DynamicBody
			)

			velCom.linearVelocity = velocity
		} else if(asteroidType == TypeComponent.TYPES.MINI_ASTEROID) {
			scoreCom.worth = 150
			position.scale.x = 0.50
			position.scale.y = 0.50
			sdBody.body = bodyFactory.makeCirclePolyBody(
					startPos.x,
					startPos.y,
					1,
					BodyFactory.STONE,
					BodyDef.BodyType.DynamicBody
			)

			velCom.linearVelocity = velocity
		}

		texture.region = asteroidTex
		sdBody.body.setUserData(entity)

		entity.add(colComp)
		entity.add(scoreCom)
		entity.add(velCom)
		entity.add(sdBody)
		entity.add(position)
		entity.add(texture)
		entity.add(type)
		engine.addEntity(entity)
	}

	@Override
	TiledMap generateBackground() {
		return null
	}

	@Override
	def createHud(SpriteBatch batch) {
		hud = new DefaultHud(batch)
		return hud
	}

}
