package com.stardecimal.game

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.maps.MapLayer
import com.badlogic.gdx.maps.MapObject
import com.badlogic.gdx.maps.objects.PolygonMapObject
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Polygon
import com.badlogic.gdx.math.RandomXS128
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef
import com.stardecimal.game.entity.components.BulletComponent
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
import com.stardecimal.game.util.simplexnoise.OpenSimplexNoise

class LevelFactory implements DefaultLevelFactory {
	private TextureRegion shotTex, platform1Tex
	RandomXS128 rand = new RandomXS128()
	Entity player
	int currentLevel = 0
	float lastPlatformX = 0
	static final short NOTHING_BIT = 0x0000
	static final short GROUND_BIT = 0x0001
	static final short PLAYER_BIT = 0x0002
	static final short BULLET_BIT = 0x0004
	OpenSimplexNoise openSim

	LevelFactory(PooledEngine en, SdAssetManager assetManager) {
		init(en, assetManager)

		TextureAtlas atlas = assetManager.manager.get(SdAssetManager.gameImages)
		shotTex = atlas.findRegion("shot")
		platform1Tex = atlas.findRegion("platform1")

		openSim = new OpenSimplexNoise(MathUtils.random(2000l))
	}

	Entity createPlayer(OrthographicCamera cam, Vector2 startPos=new Vector2(RenderingSystem.getScreenSizeInPixesWorld().x / RenderingSystem.PPM / 2 as float, 16.5 / RenderingSystem.PPM as float)) {
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
				2 / RenderingSystem.PPM as float,
				2 / RenderingSystem.PPM as float,
				BodyFactory.STEEL,
				BodyDef.BodyType.DynamicBody,
				true
		)
		sdBody.body.fixtureList.first().filterData.categoryBits = PLAYER_BIT
		sdBody.body.fixtureList.first().filterData.maskBits = GROUND_BIT

		texture.region = DFUtils.makeTextureRegion(2, 2, '#ffffff')
		position.scale.x = 1000
		position.scale.y = 1000

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
		bul.xVel = linearVelocity.x
		bul.yVel = linearVelocity.y

		sdBody.body = bodyFactory.makeCirclePolyBody(
				startPos.x,
				startPos.y,
				1 / RenderingSystem.PPM as float,
				BodyFactory.STONE,
				BodyDef.BodyType.DynamicBody,
				false,
				true
		)
		sdBody.body.fixtureList.first().filterData.categoryBits = BULLET_BIT
		sdBody.body.fixtureList.first().filterData.maskBits = (short) (PLAYER_BIT | GROUND_BIT)

		type.type = TypeComponent.TYPES.BULLET
		sdBody.body.bullet = true
		sdBody.body.setUserData(entity)
		texture.region = shotTex
		position.scale.x = 500
		position.scale.y = 500

		bul.owner = owner
		bul.maxLife = 3

		//TODO: figure out why not colliding off each other?

//		SoundEffectComponent soundCom = engine.createComponent(SoundEffectComponent)
//		soundCom.soundEffect = shot
//		soundCom.play()
//		entity.add(soundCom)


		entity.add(bul)
		entity.add(sdBody)
		entity.add(position)
		entity.add(texture)
		entity.add(colComp)
		entity.add(type)
		engine.addEntity(entity)
	}

	void generateLevel(int yLevel) {
		while(yLevel > currentLevel) {
//			for(int i = 10; i < 50; i = i + 10){
//				generateSingleColumn(i)
				generateSingleColumn(currentLevel)
//			}
			currentLevel++
		}
	}

	float genNForL(int level, int height) {
		return openSim.eval(height, level)
	}

	void generateSingleColumn(int i) {
		float minPlatformY = 2.5
		float maxPlatformY = 20
		int startOffset = 35
		int offset = 15 * i
		int range = 10
		lastPlatformX = lastPlatformX ?: startOffset

		if(genNForL(i,currentLevel) > -0.5f) {
			float newPlatformX = startOffset + offset as float
			float newPlatformY = genNForL(i * 100, currentLevel) * range + 10 as float
//			log.debug("test: ${genNForL(i * 100, currentLevel)}, x: ${newPlatformX}, y: ${newPlatformY}, test2: ${newPlatformX - lastPlatformX}")
			if(newPlatformX - lastPlatformX > 30) {
				newPlatformX = newPlatformX + 30 as float
			}
			lastPlatformX = newPlatformX

			//Keep platform y withing parameters.
			if(newPlatformY < minPlatformY) {
				newPlatformY = minPlatformY
			} else if(newPlatformY > maxPlatformY) {
				newPlatformY = maxPlatformY
			}

			buildPlatform(newPlatformX, newPlatformY)
		}
	}

	void buildPlatform(float x, float y) {
		Entity entity = engine.createEntity()
		SdBodyComponent sdBody = engine.createComponent(SdBodyComponent)
		TypeComponent type = engine.createComponent(TypeComponent)
		TransformComponent position = engine.createComponent(TransformComponent)
		TextureComponent texture = engine.createComponent(TextureComponent)

		sdBody.body = bodyFactory.makeBoxPolyBody(
				x / RenderingSystem.PPM as float,
				y / RenderingSystem.PPM as float,
				6 / RenderingSystem.PPM as float,
				1 / RenderingSystem.PPM as float,
				BodyFactory.STONE,
				BodyDef.BodyType.StaticBody
		)

		sdBody.body.fixtureList.first().filterData.categoryBits = GROUND_BIT
		sdBody.body.fixtureList.first().filterData.maskBits = (short) (PLAYER_BIT | BULLET_BIT) // can combine with | if multiple i.e. GROUND_BIT | PLAYER_BIT
		sdBody.body.setUserData(entity)
		type.type = TypeComponent.TYPES.SCENERY
		texture.region = platform1Tex
		position.scale.x = 680
		position.scale.y = 720

		entity.add(texture)
		entity.add(position)
		entity.add(type)
		entity.add(sdBody)
		engine.addEntity(entity)
	}

	void buildMap() {
		MapLayer collisionLayer = (MapLayer) map.layers.find { it.name == 'collision' }
		collisionLayer.objects.each { MapObject mapObject ->
			Entity entity = engine.createEntity()
			SdBodyComponent sdBody = engine.createComponent(SdBodyComponent)
			TypeComponent type = engine.createComponent(TypeComponent)
			TransformComponent position = engine.createComponent(TransformComponent)

			if(mapObject instanceof RectangleMapObject) {
				Rectangle rectangle = adjustRectangleDimensions2(mapObject.rectangle)
				sdBody.body = bodyFactory.makeBoxPolyBody(
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
				sdBody.body = bodyFactory.makePolygonShapeBody(
						polygon.vertices,
						polygon.x,
						polygon.y,
						BodyFactory.STONE,
						BodyDef.BodyType.StaticBody
				)
			}


			sdBody.body.fixtureList.first().filterData.categoryBits = GROUND_BIT
			sdBody.body.fixtureList.first().filterData.maskBits = (short) (PLAYER_BIT | BULLET_BIT) // can combine with | if multiple i.e. GROUND_BIT | PLAYER_BIT
			sdBody.body.setUserData(entity)
			type.type = TypeComponent.TYPES.SCENERY

			entity.add(position)
			entity.add(type)
			entity.add(sdBody)
			engine.addEntity(entity)
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
		Rectangle newRectangle = new Rectangle()
		newRectangle.x = rectangle.x * RenderingSystem.PIXELS_TO_METRES * 2 as float
		newRectangle.y = rectangle.y * RenderingSystem.PIXELS_TO_METRES * 2 as float
		newRectangle.width = rectangle.width * RenderingSystem.PIXELS_TO_METRES * 2 as float
		newRectangle.height = rectangle.height * RenderingSystem.PIXELS_TO_METRES * 2 as float
		return newRectangle
	}

	//Quite strange, I had to multiply the rectangle stuff by 2, but not the polygon??
	static Polygon adjustPolygonDimensions(Polygon polygon) {
		Polygon newPolygon = new Polygon()
		float x = polygon.x * RenderingSystem.PIXELS_TO_METRES as float
		float y = polygon.y * RenderingSystem.PIXELS_TO_METRES as float
		newPolygon.setPosition(x, y)

		float[] vertices = polygon.vertices //might need to get transformedVertices at some point, seems to work now
		def adjustedVertices = []
		vertices.each {
			adjustedVertices.add(it * RenderingSystem.PIXELS_TO_METRES as float)
		}

		newPolygon.vertices = adjustedVertices

		return newPolygon
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
