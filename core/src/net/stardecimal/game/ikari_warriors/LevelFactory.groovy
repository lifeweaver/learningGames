package net.stardecimal.game.ikari_warriors

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.math.RandomXS128
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.utils.Array
import net.stardecimal.game.BodyFactory
import net.stardecimal.game.DFUtils
import net.stardecimal.game.DefaultLevelFactory
import net.stardecimal.game.ParticleEffectManager
import net.stardecimal.game.entity.components.BulletComponent
import net.stardecimal.game.entity.components.CollisionComponent
import net.stardecimal.game.entity.components.Mapper
import net.stardecimal.game.entity.components.ParticleEffectComponent
import net.stardecimal.game.entity.components.PlayerComponent
import net.stardecimal.game.entity.components.SdBodyComponent
import net.stardecimal.game.entity.components.SoundEffectComponent
import net.stardecimal.game.entity.components.TextureComponent
import net.stardecimal.game.entity.components.TransformComponent
import net.stardecimal.game.entity.components.TypeComponent
import net.stardecimal.game.entity.systems.RenderingSystem
import net.stardecimal.game.loader.SdAssetManager

class LevelFactory implements DefaultLevelFactory {
	private TextureRegion playerTex, shotTex, grenadeTex
	Sound shot, grenadeBoom, grenadeWhistle
	RandomXS128 rand = new RandomXS128()
	Entity player
	TiledMapTileLayer collisionLayer
	int maxPlayerBullets = 99
	int maxPlayerGrenades = 99
	int playerBullets = maxPlayerBullets
	int playerGrenades = maxPlayerGrenades

	LevelFactory(PooledEngine en, SdAssetManager assetManager) {
		init(en, assetManager)

		//Specific textures
		TextureAtlas atlas = assetManager.manager.get(SdAssetManager.gameImages)
		atlas.findRegion("ikari_warriors/")

		playerTex = DFUtils.makeTextureRegion(1, 1.25, '#ffffff')
		shotTex = atlas.findRegion("ikari_warriors/shot")
		grenadeTex = atlas.findRegion("ikari_warriors/grenade")
		shot = assetManager.manager.get(SdAssetManager.ikariWarriorsShot)
		grenadeBoom = assetManager.manager.get(SdAssetManager.ikariWarriorsGrenade)
		grenadeWhistle = assetManager.manager.get(SdAssetManager.ikariWarriorsGrenadeWhistle)
		pem.addParticleEffect(ParticleEffectManager.EXPLOSION, assetManager.manager.get(SdAssetManager.ikariWarriorsExplosionParticle))

		log.info("level factory initialized")
	}

	Entity createPlayer(OrthographicCamera cam) {
		Entity entity = engine.createEntity()
		SdBodyComponent sdBody = engine.createComponent(SdBodyComponent)
		TransformComponent position = engine.createComponent(TransformComponent)
		TextureComponent texture = engine.createComponent(TextureComponent)
		PlayerComponent playerCom = engine.createComponent(PlayerComponent)
		CollisionComponent colCom = engine.createComponent(CollisionComponent)
		TypeComponent type = engine.createComponent(TypeComponent)
		Vector2 screenSize = RenderingSystem.getScreenSizeInPixesWorld()

		playerCom.cam = cam
		sdBody.body = bodyFactory.makeBoxPolyBody(
				screenSize.x / 2 as float,
				16.5,
				1,
				1.25,
				BodyFactory.STEEL,
				BodyDef.BodyType.DynamicBody,
				true
		)

		texture.region = playerTex
		position.scale.x = 15
		position.scale.y = 15

		type.type = TypeComponent.TYPES.PLAYER
		sdBody.body.setUserData(entity)

		entity.add(colCom)
		entity.add(sdBody)
		entity.add(position)
		entity.add(texture)
		entity.add(playerCom)
		entity.add(type)
		engine.addEntity(entity)
		player = entity

		return entity
	}

	void playerShoot() {
		if(playerBullets > 0) {
			playerBullets--
			Body body = Mapper.bCom.get(player).body
			createShot(body.position, body.angle)
		}
	}

	void createShot(Vector2 startPos, float angle, BulletComponent.Owner owner=BulletComponent.Owner.PLAYER) {
		Entity entity = engine.createEntity()
		SdBodyComponent sdBody = engine.createComponent(SdBodyComponent)
		TransformComponent position = engine.createComponent(TransformComponent)
		TextureComponent texture = engine.createComponent(TextureComponent)
		CollisionComponent colComp = engine.createComponent(CollisionComponent)
		TypeComponent type = engine.createComponent(TypeComponent)
		BulletComponent bul = engine.createComponent(BulletComponent)

		Vector2 linearVelocity = new Vector2()
		DFUtils.angleToVector(linearVelocity, angle)
		Vector2 shotStartPos = new Vector2(linearVelocity.x, linearVelocity.y).add(startPos)
		linearVelocity.x += linearVelocity.x * 15
		linearVelocity.y += linearVelocity.y * 15
		bul.xVel = linearVelocity.x
		bul.yVel = linearVelocity.y

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
		position.scale.x = 40
		position.scale.y = 40

		bul.owner = owner
		bul.maxLife = 1.25

		SoundEffectComponent soundCom = engine.createComponent(SoundEffectComponent)
		soundCom.soundEffect = shot
		soundCom.play()
		entity.add(soundCom)


		entity.add(bul)
		entity.add(sdBody)
		entity.add(position)
		entity.add(texture)
		entity.add(colComp)
		entity.add(type)
		engine.addEntity(entity)
	}

	void playerGrenade(Vector2 startPos, float angle) {
		if(playerGrenades > 0) {
			playerGrenades--
			createGrenade(startPos, angle)
		}
	}

	void createGrenade(Vector2 startPos, float angle, BulletComponent.Owner owner=BulletComponent.Owner.PLAYER) {
		Entity entity = engine.createEntity()
		SdBodyComponent sdBody = engine.createComponent(SdBodyComponent)
		TransformComponent position = engine.createComponent(TransformComponent)
		TextureComponent texture = engine.createComponent(TextureComponent)
		CollisionComponent colComp = engine.createComponent(CollisionComponent)
		TypeComponent type = engine.createComponent(TypeComponent)
		BulletComponent bul = engine.createComponent(BulletComponent)

		Vector2 linearVelocity = new Vector2()
		DFUtils.angleToVector(linearVelocity, angle)
		Vector2 grenadeStartPos = new Vector2(linearVelocity.x, linearVelocity.y).add(startPos)
		linearVelocity.x += linearVelocity.x * 9
		linearVelocity.y += linearVelocity.y * 9
		bul.xVel = linearVelocity.x
		bul.yVel = linearVelocity.y

		sdBody.body = bodyFactory.makeBoxPolyBody(
				grenadeStartPos.x,
				grenadeStartPos.y,
				0.25,
				0.5,
				BodyFactory.STONE,
				BodyDef.BodyType.DynamicBody,
				true
		)

		type.type = TypeComponent.TYPES.GRENADE
		sdBody.body.bullet = true
		sdBody.body.setUserData(entity)
		texture.region = grenadeTex
		position.scale.x = 40
		position.scale.y = 40

		bul.owner = owner
		bul.maxLife = 1.7

		SoundEffectComponent soundCom = engine.createComponent(SoundEffectComponent)
		soundCom.soundEffect = grenadeWhistle
		soundCom.playingVolume = 0.25
		soundCom.play()
		entity.add(soundCom)

		entity.add(bul)
		entity.add(sdBody)
		entity.add(position)
		entity.add(texture)
		entity.add(colComp)
		entity.add(type)
		engine.addEntity(entity)
	}

	Entity createBoom(Vector2 boomCenter) {
		log.debug("boom at: ${boomCenter}")
		Entity entity = engine.createEntity()
		SdBodyComponent sdBody = engine.createComponent(SdBodyComponent)
		TypeComponent type = engine.createComponent(TypeComponent)
		ParticleEffectComponent pec = engine.createComponent(ParticleEffectComponent)
		CollisionComponent colComp = engine.createComponent(CollisionComponent)

		float explosionRange = 5
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

		grenadeBoom.play()
		return entity
	}


	@Override
	TiledMap generateBackground() {
		map = assetManager.manager.get(SdAssetManager.ikariWarriorsMap)
		collisionLayer = (TiledMapTileLayer) map.getLayers().first()
		return map
	}

	@Override
	def createHud(SpriteBatch batch) {
		hud = new IkariWarriorsHud(batch)
		return hud
	}
}
