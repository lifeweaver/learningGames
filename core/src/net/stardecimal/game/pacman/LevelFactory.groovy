package net.stardecimal.game.pacman

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.audio.Sound
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
import net.stardecimal.game.entity.components.ScoreComponent
import net.stardecimal.game.entity.components.SdBodyComponent
import net.stardecimal.game.entity.components.TextureComponent
import net.stardecimal.game.entity.components.TransformComponent
import net.stardecimal.game.entity.components.TypeComponent
import net.stardecimal.game.entity.systems.RenderingSystem
import net.stardecimal.game.loader.SdAssetManager
import net.stardecimal.game.pacman.entity.component.PowerUpComponent

class LevelFactory implements DefaultLevelFactory {
	private TextureRegion playerTex, ghost1Tex, ghost2Tex, ghost3Tex, ghost4Tex
	private Animation<TextureRegion> pacmanAnimation
	Sound eatPelletA, eatPelletB, nextPelletSound, gameOverPacMan, powerUp
	long powerUpId
//	RandomXS128 rand = new RandomXS128()
	Entity player
	TiledMapTileLayer collisionLayer
	ComponentMapper<PowerUpComponent> powerCom = ComponentMapper.getFor(PowerUpComponent.class)

	LevelFactory(PooledEngine en, SdAssetManager assetManager) {
		init(en, assetManager)

		//Specific textures
		TextureAtlas atlas = assetManager.manager.get(SdAssetManager.gameImages)
		atlas.findRegion("pacman/")

		playerTex = atlas.findRegion("pacman/player")
		pacmanAnimation = new Animation<TextureRegion>(0.1f, atlas.findRegions("pacman/pacman"), Animation.PlayMode.LOOP)
		eatPelletA = assetManager.manager.get(SdAssetManager.eatPelletA)
		eatPelletB = assetManager.manager.get(SdAssetManager.eatPelletB)
		gameOverPacMan = assetManager.manager.get(SdAssetManager.gameOverPacMan)
		powerUp = assetManager.manager.get(SdAssetManager.powerUp)
		nextPelletSound = eatPelletA

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
		yAxisCentering(sdBody)

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

	void yAxisCentering(SdBodyComponent playerBody) {
		float tileHeight = collisionLayer.tileHeight * RenderingSystem.PIXELS_TO_METRES as float
		float yAxisMisalignment = (playerBody.body.position.y / tileHeight) % 1 as float

		if(Math.abs(tileHeight / 2 - yAxisMisalignment) < 0.01) {
			//do nothing
			return
		} else if(yAxisMisalignment > tileHeight / 2) {
			yAxisMisalignment = yAxisMisalignment - tileHeight / 2 as float
			playerBody.body.setTransform(playerBody.body.position.x, playerBody.body.position.y - yAxisMisalignment as float, 0)
		} else {
			yAxisMisalignment = tileHeight / 2 - yAxisMisalignment as float
			playerBody.body.setTransform(playerBody.body.position.x, playerBody.body.position.y + yAxisMisalignment as float, 0)
		}
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

	Vector2 gamePosition(int x, int y) {
		float tileHeight = collisionLayer.tileHeight * RenderingSystem.PIXELS_TO_METRES as float
		float tileWidth = collisionLayer.tileWidth * RenderingSystem.PIXELS_TO_METRES as float
		def offsetX = collisionLayer.offsetX / (1 / RenderingSystem.PIXELS_TO_METRES)

		float gameX = x * tileWidth + offsetX as float
		float gameY = y / (collisionLayer.height / (collisionLayer.height * tileHeight)) as float
		return new Vector2(gameX, gameY)
	}

	boolean isCellBlocked(float gameX, float gameY) {
		Vector2 tilePos = tilePosition(gameX, gameY)
		return isCellBlocked(tilePos)
	}

	boolean isCellBlocked(Vector2 tilePos) {
		return isCell('blocked', true, tilePos)
	}

	boolean isCell(String property, value, Vector2 tilePos) {
		TiledMapTileLayer.Cell cell = getCell(tilePos)
		return cell && cell.tile ? cell.tile.properties.get(property) == value : false
	}

	TiledMapTileLayer.Cell getCell(Vector2 tilePos) {
		int tileX = tilePos.x as int
		int tileY = tilePos.y as int

		return collisionLayer.getCell(tileX, tileY)
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
