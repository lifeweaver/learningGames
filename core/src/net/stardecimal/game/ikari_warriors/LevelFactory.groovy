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
import net.stardecimal.game.entity.systems.RenderingSystem
import net.stardecimal.game.loader.SdAssetManager

class LevelFactory implements DefaultLevelFactory {
	private TextureRegion playerTex, shotTex
	Sound shot
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

//		playerTex = atlas.findRegion("ikari_warriors/player")
		playerTex = DFUtils.makeTextureRegion(1, 1.25, '#ffffff')
		shotTex = atlas.findRegion("ikari_warriors/shot")
		shot = assetManager.manager.get(SdAssetManager.ikariWarriorsShot)

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
