package net.stardecimal.game.asteroids

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.RandomXS128
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import net.stardecimal.game.BodyFactory
import net.stardecimal.game.DFUtils
import net.stardecimal.game.DefaultLevelFactory
import net.stardecimal.game.entity.components.BulletComponent
import net.stardecimal.game.entity.components.CollisionComponent
import net.stardecimal.game.entity.components.Mapper
import net.stardecimal.game.entity.components.PlayerComponent
import net.stardecimal.game.entity.components.ScoreComponent
import net.stardecimal.game.entity.components.SdBodyComponent
import net.stardecimal.game.entity.components.SoundEffectComponent
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

		log.info("level factory initialized")
	}

	void createPlayer(OrthographicCamera cam) {
		Entity entity = engine.createEntity()
		SdBodyComponent sdBody = engine.createComponent(SdBodyComponent)
		TransformComponent position = engine.createComponent(TransformComponent)
		TextureComponent texture = engine.createComponent(TextureComponent)
		PlayerComponent playerCom = engine.createComponent(PlayerComponent)
		TypeComponent type = engine.createComponent(TypeComponent)
		VelocityComponent velCom = engine.createComponent(VelocityComponent)
		Vector2 screenSize = RenderingSystem.getScreenSizeInMeters()

		playerCom.cam = cam
		sdBody.body = bodyFactory.makeBoxPolyBody(
				screenSize.x / RenderingSystem.PPM / 2 as float,
				screenSize.y / RenderingSystem.PPM / 2 as float,
				1,
				1.5f,
				BodyFactory.STEEL,
				BodyDef.BodyType.DynamicBody,
				false
		)

		texture.region = playerTex
		type.type = TypeComponent.TYPES.PLAYER
		sdBody.body.setUserData(entity)
		velCom.removeAfterProcessing = false

		entity.add(velCom)
		entity.add(sdBody)
		entity.add(position)
		entity.add(texture)
		entity.add(playerCom)
		entity.add(type)
		engine.addEntity(entity)
		player = entity
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
		velCom.linearVelocity.x += velCom.linearVelocity.x * 10
		velCom.linearVelocity.y += velCom.linearVelocity.y * 10

		sdBody.body = bodyFactory.makeBoxPolyBody(
				startPos.x = velCom.linearVelocity.x > 0 ? startPos.x + 1 as float : startPos.x - 1 as float,
				startPos.y = velCom.linearVelocity.y > 0 ? startPos.y + 1 as float : startPos.y - 1 as float,
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

	void createAsteroid(Vector2 startPos=null, Vector2 velocity=null, boolean mini=false) {
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
		Vector3 playerPos = Mapper.transCom.get(player).position
		if(Math.abs(playerPos.x - randX) < 3 || Math.abs(playerPos.y - randY) < 3) {
			randX += 4
			randY += 4
		}

		if(!mini) {
			scoreCom.worth = 10
			type.type = TypeComponent.TYPES.ASTEROID
			position.scale.x = 0.65
			position.scale.y = 0.65
			sdBody.body = bodyFactory.makeCirclePolyBody(
					randX,
					randY,
					1.25,
					BodyFactory.STONE,
					BodyDef.BodyType.DynamicBody
			)
			velCom.linearVelocity.x = rand.nextInt(10)
			velCom.linearVelocity.y = rand.nextInt(10)
		} else {
			scoreCom.worth = 5
			type.type = TypeComponent.TYPES.MINI_ASTEROID
			position.scale.x = 0.30
			position.scale.y = 0.30
			sdBody.body = bodyFactory.makeCirclePolyBody(
					startPos.x,
					startPos.y,
					0.75,
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

	}

}
