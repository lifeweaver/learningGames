package com.stardecimal.game

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.maps.MapLayer
import com.badlogic.gdx.maps.MapObject
import com.badlogic.gdx.maps.objects.PolygonMapObject
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.math.Polygon
import com.badlogic.gdx.math.RandomXS128
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import com.stardecimal.game.entity.components.CollisionComponent
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
	static final short NOTHING_BIT = 0x0000
	static final short GROUND_BIT = 0x0001
	static final short PLAYER_BIT = 0x0010

	LevelFactory(PooledEngine en, SdAssetManager assetManager) {
		init(en, assetManager)

		//Specific textures
//		paddleTex = DFUtils.makeTextureRegion(1, 1, '#ffffff')
//		pingPongTex = DFUtils.makeTextureRegion(1, 1, '#ffffff')
//		boundaryTex = DFUtils.makeTextureRegion(RenderingSystem.getScreenSizeInMeters().x / RenderingSystem.PPM as float, 0.1f, '#ffffff')
//		enemyScoreWallTex = DFUtils.makeTextureRegion(0.1, 1, '#000000')
	}

	Entity createPlayer(OrthographicCamera cam, Vector2 startPos=new Vector2(RenderingSystem.getScreenSizeInPixesWorld().x / 2 as float, 16.5)) {
		Entity entity = engine.createEntity()
		SdBodyComponent sdBody = engine.createComponent(SdBodyComponent)
		TransformComponent position = engine.createComponent(TransformComponent)
		TextureComponent texture = engine.createComponent(TextureComponent)
		PlayerComponent playerCom = engine.createComponent(PlayerComponent)
		TypeComponent type = engine.createComponent(TypeComponent)
		CollisionComponent colCom = engine.createComponent(CollisionComponent)

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
		sdBody.body.fixtureList.first().filterData.categoryBits = PLAYER_BIT
		sdBody.body.fixtureList.first().filterData.maskBits = GROUND_BIT

		texture.region = DFUtils.makeTextureRegion(2, 2, '#ffffff')
//		position.scale.x = 15
//		position.scale.y = 15

		type.type = TypeComponent.TYPES.PLAYER
		sdBody.body.setUserData(entity)
		sdBody.invulnerabilityTime = 1

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

	void buildMap() {
		MapLayer collisionLayer = (MapLayer) map.layers.find { it.name == 'collision' }
		collisionLayer.objects.each { MapObject mapObject ->
			Body body = null

			if(mapObject instanceof RectangleMapObject) {
				Rectangle rectangle = adjustRectangleDimensions2(mapObject.rectangle)
				body = bodyFactory.makeBoxPolyBody(
						rectangle.x,
						rectangle.y,
						rectangle.width,
						rectangle.height,
						BodyFactory.STONE,
						BodyDef.BodyType.StaticBody
				)
			}

			if(mapObject instanceof PolygonMapObject) {
				Polygon polygon = adjustPolygonDimensions(mapObject.polygon)
				body = bodyFactory.makePolygonShapeBody(
						polygon.vertices,
						polygon.x,
						polygon.y,
						BodyFactory.STONE,
						BodyDef.BodyType.StaticBody
				)
			}


			body.fixtureList.first().filterData.categoryBits = GROUND_BIT
			body.fixtureList.first().filterData.maskBits = PLAYER_BIT // can combine with | if multiple i.e. GROUND_BIT | PLAYER_BIT
		}
	}

//	static Rectangle adjustRectangleDimensions(Rectangle rectangle) {
//		rectangle.x = rectangle.x / RenderingSystem.PPM
//		rectangle.y = rectangle.y / RenderingSystem.PPM
//		rectangle.width = rectangle.width / RenderingSystem.PPM
//		rectangle.height = rectangle.height / RenderingSystem.PPM
//		return rectangle
//	}

	//Not sure why this works but the above doesn't..
	static Rectangle adjustRectangleDimensions2(Rectangle rectangle) {
		rectangle.x = rectangle.x * RenderingSystem.PIXELS_TO_METRES * 2 as float
		rectangle.y = rectangle.y * RenderingSystem.PIXELS_TO_METRES * 2 as float
		rectangle.width = rectangle.width * RenderingSystem.PIXELS_TO_METRES * 2 as float
		rectangle.height = rectangle.height * RenderingSystem.PIXELS_TO_METRES * 2 as float
		return rectangle
	}

	//Quite strange, I had to multiply the rectangle stuff by 2, but not the polygon??
	static Polygon adjustPolygonDimensions(Polygon polygon) {
		float x = polygon.x * RenderingSystem.PIXELS_TO_METRES as float
		float y = polygon.y * RenderingSystem.PIXELS_TO_METRES as float
		polygon.setPosition(x, y)

		float[] vertices = polygon.vertices //might need to get transformedVertices at some point, seems to work now
		def adjustedVertices = []
		vertices.each {
			adjustedVertices.add(it * RenderingSystem.PIXELS_TO_METRES as float)
		}

		polygon.vertices = adjustedVertices

		return polygon
	}

	TiledMap generateBackground() {
		map = assetManager.manager.get(SdAssetManager.gameMap)
		return map
	}

	@Override
	def createHud(SpriteBatch batch) {
		hud = new DefaultHud(batch)
		return hud
	}
}
