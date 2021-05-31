package net.stardecimal.game.pacman

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef
import net.stardecimal.game.BodyFactory
import net.stardecimal.game.DefaultHud
import net.stardecimal.game.DefaultLevelFactory
import net.stardecimal.game.entity.components.CollisionComponent
import net.stardecimal.game.entity.components.PlayerComponent
import net.stardecimal.game.entity.components.SdBodyComponent
import net.stardecimal.game.entity.components.TextureComponent
import net.stardecimal.game.entity.components.TransformComponent
import net.stardecimal.game.entity.components.TypeComponent
import net.stardecimal.game.entity.systems.RenderingSystem
import net.stardecimal.game.loader.SdAssetManager

class LevelFactory implements DefaultLevelFactory {
	private TextureRegion playerTex, ghost1Tex, ghost2Tex, ghost3Tex, ghost4Tex
	private Animation<TextureRegion> pacmanAnimation
//	Sound enemy4Theme, enemyBlownUp, playerBlownUp, playerFiring, background
//	RandomXS128 rand = new RandomXS128()
	Entity player
	TiledMapTileLayer collisionLayer

	LevelFactory(PooledEngine en, SdAssetManager assetManager) {
		init(en, assetManager)

		//Specific textures
		TextureAtlas atlas = assetManager.manager.get(SdAssetManager.gameImages)
		atlas.findRegion("pacman/")

		playerTex = atlas.findRegion("pacman/player")
		pacmanAnimation = new Animation<TextureRegion>(0.1f, atlas.findRegions("pacman/pacman"), Animation.PlayMode.LOOP)

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
		sdBody.body = bodyFactory.makeCirclePolyBody(
				screenSize.x / 2 as float,
				8,
				1.5,
				BodyFactory.STONE,
				BodyDef.BodyType.DynamicBody,
				true
		)

		texture.animation = pacmanAnimation
		position.scale.x = 4
		position.scale.y = 4
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

	Vector2 tilePosition(float x, float y) {
		Vector2 point = new Vector2(x, y)
		float tileHeight = collisionLayer.tileHeight * RenderingSystem.PIXELS_TO_METRES as float
		float tileWidth = collisionLayer.tileWidth * RenderingSystem.PIXELS_TO_METRES as float
		def offsetX = collisionLayer.offsetX / (1 / RenderingSystem.PIXELS_TO_METRES)

		int tileX = (point.x - offsetX) / tileWidth as int
		int tileY = point.y * (collisionLayer.height / (collisionLayer.height * tileHeight)) as int
		return new Vector2(tileX, tileY)
	}

	boolean isCellBlocked(float gameX, float gameY) {
		Vector2 tilePos = tilePosition(gameX, gameY)
		int tileX = tilePos.x as int
		int tileY = tilePos.y as int

		TiledMapTileLayer.Cell cell = collisionLayer.getCell(tileX, tileY)
//		println("x: ${x}, y: ${y}, tileX: ${tileX}, tileY: ${tileY}, test: ${cell?.tile ? cell.tile.properties.get('blocked') == true : false}")

		return cell && cell.tile ? cell.tile.properties.get('blocked') == true : false
	}

	//
	boolean isCellBlocked(Vector2 tilePos) {
		int tileX = tilePos.x as int
		int tileY = tilePos.y as int

		TiledMapTileLayer.Cell cell = collisionLayer.getCell(tileX, tileY)
		return cell && cell.tile ? cell.tile.properties.get('blocked') == true : true
	}

	@Override
	TiledMap generateBackground() {
		map = assetManager.manager.get(SdAssetManager.pacmanMap)
		collisionLayer = (TiledMapTileLayer) map.getLayers().first()
		return map
	}

	@Override
	def createHud(SpriteBatch batch) {
		hud = new DefaultHud(batch)
		return hud
	}
}