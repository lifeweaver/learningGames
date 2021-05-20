package net.stardecimal.game.tetris

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.math.RandomXS128
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef
import net.stardecimal.game.BodyFactory
import net.stardecimal.game.DefaultHud
import net.stardecimal.game.DefaultLevelFactory
import net.stardecimal.game.asteroids.entity.systems.PlayerControlSystem
import net.stardecimal.game.entity.components.PlayerComponent
import net.stardecimal.game.entity.components.SdBodyComponent
import net.stardecimal.game.entity.components.TextureComponent
import net.stardecimal.game.entity.components.TransformComponent
import net.stardecimal.game.entity.components.TypeComponent
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
		atlas.findRegion("tetris/")
//
		playerTex = atlas.findRegion("asteroids/player")
//		enemyTex = atlas.findRegion("asteroids/enemy")
//		asteroidTex = atlas.findRegion("asteroids/asteroid")
//		shotTex = DFUtils.makeTextureRegion(1, 1, '#FFFFFF')

//		enemyBlownUp = assetManager.manager.get(SdAssetManager.enemyBlownUp)
//		playerBlownUp = assetManager.manager.get(SdAssetManager.playerBlownUp)
//		playerFiring = assetManager.manager.get(SdAssetManager.playerFiring)

		log.info("level factory initialized")
	}

	void createPlayer(OrthographicCamera cam) {
		//Reset controls on death
		controller.reset()

//		Entity entity = engine.createEntity()
//		SdBodyComponent sdBody = engine.createComponent(SdBodyComponent)
//		TransformComponent position = engine.createComponent(TransformComponent)
//		TextureComponent texture = engine.createComponent(TextureComponent)
//		PlayerComponent playerCom = engine.createComponent(PlayerComponent)
//		TypeComponent type = engine.createComponent(TypeComponent)
//		Vector2 screenSize = RenderingSystem.getScreenSizeInMeters()

//		playerCom.cam = cam
//		sdBody.body = bodyFactory.makeBoxPolyBody(
//				screenSize.x / RenderingSystem.PPM / 2 as float,
//				screenSize.y / RenderingSystem.PPM / 2 as float,
//				1,
//				4,
//				BodyFactory.STEEL,
//				BodyDef.BodyType.DynamicBody,
//				false
//		)
//
//		texture.region = playerTex
//		type.type = TypeComponent.TYPES.PLAYER
//		sdBody.body.setUserData(entity)
//
//		entity.add(sdBody)
//		entity.add(position)
//		entity.add(texture)
//		entity.add(playerCom)
//		entity.add(type)
//		engine.addEntity(entity)
//		player = entity
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
