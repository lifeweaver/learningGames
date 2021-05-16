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
import com.badlogic.gdx.physics.box2d.BodyDef
import net.stardecimal.game.BodyFactory
import net.stardecimal.game.DFUtils
import net.stardecimal.game.DefaultLevelFactory
import net.stardecimal.game.entity.components.BulletComponent
import net.stardecimal.game.entity.components.CollisionComponent
import net.stardecimal.game.entity.components.Mapper
import net.stardecimal.game.entity.components.PlayerComponent
import net.stardecimal.game.entity.components.SdBodyComponent
import net.stardecimal.game.entity.components.SoundEffectComponent
import net.stardecimal.game.entity.components.TextureComponent
import net.stardecimal.game.entity.components.TransformComponent
import net.stardecimal.game.entity.components.TypeComponent
import net.stardecimal.game.entity.components.VelocityComponent
import net.stardecimal.game.entity.systems.RenderingSystem
import net.stardecimal.game.loader.SdAssetManager

class LevelFactory implements DefaultLevelFactory {
	private TextureRegion enemyTex, playerTex, shotTex
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
		//Get player position
		Vector2 startPos = Mapper.bCom.get(player).body.position
		createShot(startPos)
	}

	void createShot(Vector2 startPos) {
		//Create shot
		Entity entity = engine.createEntity()
		SdBodyComponent sdBody = engine.createComponent(SdBodyComponent)
		TransformComponent position = engine.createComponent(TransformComponent)
		TextureComponent texture = engine.createComponent(TextureComponent)
		CollisionComponent colComp = engine.createComponent(CollisionComponent)
		TypeComponent type = engine.createComponent(TypeComponent)
		BulletComponent bul = engine.createComponent(BulletComponent)
		VelocityComponent velCom = engine.createComponent(VelocityComponent)


		//TODO fix for spawning in ship direction, startPos.angleDeg()
		sdBody.body = bodyFactory.makeBoxPolyBody(
				startPos.x + 5 as float,
				startPos.y + 5 as float,
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

		//TODO: figure out velocity
		DFUtils.angleToVector(velCom.linearVelocity, startPos.angleRad())
		println("angle: ${startPos.angleRad() * MathUtils.radiansToDegrees}, linearVelocity: ${velCom.linearVelocity}, something: ${DFUtils.vectorToAngle2(new Vector2(startPos.x, startPos.y)) * MathUtils.radiansToDegrees}, something2: ${DFUtils.vectorToAngle(new Vector2(startPos.x, startPos.y)) * MathUtils.radiansToDegrees}")
		velCom.linearVelocity.x += velCom.linearVelocity.x * 10
		velCom.linearVelocity.y += velCom.linearVelocity.y * 10

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

	@Override
	TiledMap generateBackground() {
		return null
	}

	@Override
	def createHud(SpriteBatch batch) {

	}

}
