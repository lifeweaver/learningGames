package com.stardecimal.game

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.math.RandomXS128
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef
import com.stardecimal.game.entity.components.PlayerComponent
import com.stardecimal.game.entity.components.SdBodyComponent
import com.stardecimal.game.entity.components.TextureComponent
import com.stardecimal.game.entity.components.TransformComponent
import com.stardecimal.game.entity.components.TypeComponent
import com.stardecimal.game.entity.systems.RenderingSystem
import com.stardecimal.game.util.SdAssetManager
import com.stardecimal.game.util.BodyFactory
import com.stardecimal.game.util.DFUtils
import com.stardecimal.game.util.DefaultHud
import com.stardecimal.game.util.DefaultLevelFactory

class LevelFactory implements DefaultLevelFactory {
	RandomXS128 rand = new RandomXS128()
	Entity player
	TiledMapTileLayer collisionLayer

	LevelFactory(PooledEngine en, SdAssetManager assetManager) {
		init(en, assetManager)

		//Specific textures
//		paddleTex = DFUtils.makeTextureRegion(1, 1, '#ffffff')
//		pingPongTex = DFUtils.makeTextureRegion(1, 1, '#ffffff')
//		boundaryTex = DFUtils.makeTextureRegion(RenderingSystem.getScreenSizeInMeters().x / RenderingSystem.PPM as float, 0.1f, '#ffffff')
//		enemyScoreWallTex = DFUtils.makeTextureRegion(0.1, 1, '#000000')
	}

	Entity createPlayer(OrthographicCamera cam, Vector2 startPos=new Vector2(RenderingSystem.getScreenSizeInPixesWorld().x / 2 as float, 16.5)) {
		log.debug("RenderingSystem.getScreenSizeInPixesWorld().x / 2: ${RenderingSystem.getScreenSizeInPixesWorld().x / 2}")
		Entity entity = engine.createEntity()
		SdBodyComponent sdBody = engine.createComponent(SdBodyComponent)
		TransformComponent position = engine.createComponent(TransformComponent)
		TextureComponent texture = engine.createComponent(TextureComponent)
		PlayerComponent playerCom = engine.createComponent(PlayerComponent)
		TypeComponent type = engine.createComponent(TypeComponent)

		playerCom.cam = cam
		sdBody.body = bodyFactory.makeBoxPolyBody(
				startPos.x,
				startPos.y,
				2,
				2,
				BodyFactory.STEEL,
				BodyDef.BodyType.DynamicBody,
				true
		)

		texture.region = DFUtils.makeTextureRegion(2, 2, '#ffffff')
//		position.scale.x = 15
//		position.scale.y = 15

		type.type = TypeComponent.TYPES.PLAYER
		sdBody.body.setUserData(entity)
		sdBody.invulnerabilityTime = 1

		entity.add(sdBody)
		entity.add(position)
		entity.add(texture)
		entity.add(playerCom)
		entity.add(type)
		engine.addEntity(entity)
		player = entity

		return entity
	}


	TiledMap generateBackground() {
		map = assetManager.manager.get(SdAssetManager.gameMap)
		collisionLayer = (TiledMapTileLayer) map.getLayers().first()
		return map
	}

	@Override
	def createHud(SpriteBatch batch) {
		hud = new DefaultHud(batch)
		return hud
	}
}
