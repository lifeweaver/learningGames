package net.stardecimal.game.worm

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.maps.MapLayers
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef
import net.stardecimal.game.BodyFactory
import net.stardecimal.game.DFUtils
import net.stardecimal.game.DefaultLevelFactory
import net.stardecimal.game.entity.components.CollisionComponent
import net.stardecimal.game.worm.entity.components.PlayerComponent
import net.stardecimal.game.entity.components.SdBodyComponent
import net.stardecimal.game.entity.components.StateComponent
import net.stardecimal.game.entity.components.SteeringComponent
import net.stardecimal.game.entity.components.TextureComponent
import net.stardecimal.game.entity.components.TransformComponent
import net.stardecimal.game.entity.components.TypeComponent
import net.stardecimal.game.entity.systems.RenderingSystem
import net.stardecimal.game.loader.SdAssetManager
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class LevelFactory implements DefaultLevelFactory {
	private TextureRegion boundaryTex, wormTailTex, cellBackground
	private Texture fruitTex, wormTex
	private TiledMap background
	private static final Logger log = LoggerFactory.getLogger(LevelFactory)

	LevelFactory(PooledEngine en, SdAssetManager assetManager) {
		init(en, assetManager)

		//Specific textures
		boundaryTex = DFUtils.makeTextureRegion(RenderingSystem.getScreenSizeInMeters().x / RenderingSystem.PPM as float, 0.1f, '#ffffff')
		wormTex  = assetManager.manager.get(SdAssetManager.worm)
		fruitTex = assetManager.manager.get(SdAssetManager.fruit)
		wormTailTex = DFUtils.makeTextureRegion(2, 2, '#00137F')
		cellBackground =  DFUtils.makeTextureRegion(2, 2, '#008000')
		log.info("level factory initialized")
	}


	Entity createPlayer(OrthographicCamera cam){
		Entity entity = engine.createEntity()
		SdBodyComponent sdBody = engine.createComponent(SdBodyComponent)
		TransformComponent position = engine.createComponent(TransformComponent)
		TextureComponent texture = engine.createComponent(TextureComponent)
		PlayerComponent player = engine.createComponent(PlayerComponent)
		CollisionComponent colComp = engine.createComponent(CollisionComponent)
		TypeComponent type = engine.createComponent(TypeComponent)
		StateComponent stateCom = engine.createComponent(StateComponent)
		SteeringComponent scom = engine.createComponent(SteeringComponent)
		Vector2 screenSize = RenderingSystem.getScreenSizeInMeters()
		TiledMapTileLayer layer1 = (TiledMapTileLayer) background.layers.first()

		player.cam = cam

		sdBody.body = bodyFactory.makeCirclePolyBody(
				screenSize.x / RenderingSystem.PPM / 2 as float,
				screenSize.y / RenderingSystem.PPM / 2 as float,
				1,
				BodyFactory.NOTHING,
				BodyDef.BodyType.KinematicBody,
				true
		)

		texture.region = new TextureRegion(wormTex)
		type.type = TypeComponent.PLAYER
		stateCom.set(StateComponent.STATE_NORMAL)
		sdBody.body.setUserData(entity)
		sdBody.body.sleepingAllowed = false
		scom.body = sdBody.body

		entity.add(sdBody)
		entity.add(position)
		entity.add(texture)
		entity.add(player)
		entity.add(colComp)
		entity.add(type)
		entity.add(stateCom)
		entity.add(scom)

		engine.addEntity(entity)
		this.player = entity
		return entity
	}

	Vector2 body2tile(Vector2 playerPosition) {
		return body2tile(playerPosition.x, playerPosition.y)
	}


	Vector2 body2tile(float x, float y) {
		TiledMapTileLayer layer1 = (TiledMapTileLayer) background.layers.first()

		Vector2 mapPosition = new Vector2()
		mapPosition.x = (x / layer1.tileWidth).round(1) as float
		mapPosition.y = (y / layer1.tileHeight).round(1) as float

		return mapPosition
	}

	void changeBackground(Vector2 playerPosition, isTail=false) {
		changeBackground(playerPosition.x, playerPosition.y, isTail)
	}

	void changeBackground(float x, float y, isTail=false) {
		TiledMapTileLayer layer1 = (TiledMapTileLayer) background.layers.first()
		TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell()
		cell.setTile(new StaticTiledMapTile(isTail ? wormTailTex : cellBackground))
//		Vector2 mapPosition = body2tile(x, y)

		layer1.setCell(x as int, y as int, cell)
	}

	private static Vector2 adjustPosition(double bodyAngle, float x, float y) {
		switch(bodyAngle) {
			case 0:
				y--
				break

			case 180:
				y++
				break

			case 90:
				x++
				break

			case 270:
				x--
				break
		}

		return new Vector2(x, y)
	}

	void resetBackground() {
		TiledMapTileLayer layer1 = (TiledMapTileLayer) background.layers.first()
		TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell(tile: new StaticTiledMapTile(cellBackground))
		Vector2 screenSize = RenderingSystem.getScreenSizeInMeters()
		for (int row = 0; row < screenSize.y; row++) {
			for (int col = 0; col < screenSize.x; col++) {
				layer1.setCell(col, row, cell)
			}
		}
	}

	void applyBackground(Vector2 position, double bodyAngle, int length, List<float[]> rotations) {
		//Convert the body position to tile position
		Vector2 tilePosition = body2tile(position.x, position.y)

		//First segment, use body position, and current angle
		Vector2 oldPosition = tilePosition
		Vector2 adjustedPosition = oldPosition
		double lastAngle = bodyAngle

		//Save off copy
		List<float[]> tempCopy = []
		rotations.reverse().each {
			tempCopy << it
		}

		if(!tempCopy.isEmpty()) {
			log.info("angle: ${lastAngle}, rotations: ${tempCopy}, length: ${length}, bodyPosition: ${tilePosition}")

			//Correct angle, sometimes this runs before the rotations is updated
			if(tempCopy.first()) {
				lastAngle = Math.abs(tempCopy.first()[2] - lastAngle) < 91 ? lastAngle : tempCopy.first()[3]
				log.info("applying angle correction if necessary: ${lastAngle}")
			}
		}


		//Reset background so we remove all the old tails
		resetBackground()

		//TODO: figure out why it only works with one turn?
		// I'm thinking it's because every angle after the first needs to use the last rotation as the start point, instead of the body position?
		// Or maybe the rotation list is getting stomped on?
		//Loop over the length
		length.times {
			def firstRotation = !tempCopy.isEmpty() ? tempCopy.first() : null
			def epsilon = 0.7

			if(tempCopy.size() > 1) {
				def blue = 6
			}


			log.info("firstRotation: ${firstRotation}")
			if(firstRotation && Math.abs(firstRotation[0] - oldPosition.x.round(1)) < epsilon && Math.abs(firstRotation[1] - oldPosition.y.round(1)) < epsilon) {
				//Since we found a match, remove it
				tempCopy.remove(0)
				def jtest = lastAngle
				def jtest2 = oldPosition
				lastAngle = firstRotation[2]
				oldPosition = adjustedPosition
				adjustedPosition = adjustPosition(lastAngle, adjustedPosition.x, adjustedPosition.y)
				log.info("${it.toString().padLeft(2, '0')}: lastAngle: ${jtest}, newAngle: ${firstRotation[2]}, OldPosition: ${oldPosition}, adjustedPosition: ${adjustedPosition}, absX: ${Math.abs(firstRotation[0] - jtest2.x.round(1))}, absY: ${Math.abs(firstRotation[1] - jtest2.y.round(1))}")
			} else {
				def jtest2 = oldPosition
				oldPosition = adjustedPosition
				adjustedPosition = adjustPosition(lastAngle, adjustedPosition.x, adjustedPosition.y)
				def jtest3 = ''
				if(firstRotation) {
					jtest3 = ", absX: ${Math.abs(firstRotation[0] - jtest2.x.round(1))}, absY: ${Math.abs(firstRotation[1] - jtest2.y.round(1))}"
				}
				log.info("${it.toString().padLeft(2, '0')}: lastAngle: ${lastAngle}, OldPosition: ${oldPosition}, adjustedPosition: ${adjustedPosition}${jtest3}")
			}

			changeBackground(adjustedPosition, true)
		}
		log.info("done\n")

		//Remove unused rotations
//		if(!tempCopy.isEmpty()) {
//			PlayerComponent playerComponent = ComponentMapper.getFor(PlayerComponent.class).get(player)
//			tempCopy.each {
//				log.info("Removing: ${it}")
//				playerComponent.rotations.remove(it)
//			}
//		}

		engine.getSystem(RenderingSystem).addTiledMapBackground(background)
	}

	Entity createFruit() {
		Entity entity = engine.createEntity()
		SdBodyComponent sdBody = engine.createComponent(SdBodyComponent)
		TransformComponent position = engine.createComponent(TransformComponent)
		TextureComponent texture = engine.createComponent(TextureComponent)
		TypeComponent type = engine.createComponent(TypeComponent)
		Vector2 screenSize = RenderingSystem.getScreenSizeInMeters()

		sdBody.body = bodyFactory.makeCirclePolyBody(
				randomPos(0, screenSize.x) / RenderingSystem.PPM / 2 as float,
				randomPos(0, screenSize.y) / RenderingSystem.PPM / 2 as float,
				1f,
				BodyFactory.STONE,
				BodyDef.BodyType.DynamicBody,
				true
		)
		texture.region = new TextureRegion(fruitTex)

		type.type = TypeComponent.SCORE_WALL
		sdBody.body.setUserData(entity)

		entity.add(sdBody)
		entity.add(position)
		entity.add(texture)
		entity.add(type)

		engine.addEntity(entity)
		return entity
	}

	TiledMap generateBackground() {
		Vector2 screenSize = RenderingSystem.getScreenSizeInMeters()
		TiledMap map = new TiledMap()
		MapLayers layers = map.layers
		//Changed width/height to 1 to see if that made everything easier
		TiledMapTileLayer layer1 = new TiledMapTileLayer(screenSize.x as int, screenSize.y as int, 1, 1)

		for (int row = 0; row < screenSize.y; row++) {
			for (int col = 0; col < screenSize.x; col++) {
				TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell()
				cell.setTile(new StaticTiledMapTile(cellBackground))
				layer1.setCell(col, row, cell)
			}
		}

		layers.add(layer1)
		background = map

		return map
	}

	void createBoundaries() {
		Vector2 screenSize = RenderingSystem.getScreenSizeInMeters()
		float boundaryWidth = 0.1f

		//Floor
		createBoundary(new Vector2(screenSize.x, 0.1f), screenSize.x, boundaryWidth)

		//Ceiling
		createBoundary(new Vector2(screenSize.x, screenSize.y * 2 as float), screenSize.x, boundaryWidth)

		//Right wall
		createBoundary(new Vector2(screenSize.x * 2 as float, screenSize.y * 2 as float), boundaryWidth, screenSize.y)

		//Left wall
		createBoundary(new Vector2(0, 0), boundaryWidth, screenSize.y)
	}

	void createBoundary(Vector2 pos, float width, float height) {
		Entity entity = engine.createEntity()
		SdBodyComponent sdBody = engine.createComponent(SdBodyComponent)
		TransformComponent position = engine.createComponent(TransformComponent)
		TextureComponent texture = engine.createComponent(TextureComponent)
		TypeComponent type = engine.createComponent(TypeComponent)

		//Divide by the PPM then by 22 unless they are zero
		float x = pos.x != 0 ? pos.x / RenderingSystem.PPM / 2 as float : pos.x
		float y = pos.y != 0 ? pos.y / RenderingSystem.PPM / 2 as float : pos.y

		position.position.set(x, y, 0)
		texture.region = boundaryTex
		type.type = TypeComponent.SCENERY
		sdBody.body = bodyFactory.makeBoxPolyBody(x, y, width, height, BodyFactory.STONE, BodyDef.BodyType.StaticBody)

		entity.add(sdBody)
		entity.add(texture)
		entity.add(position)
		entity.add(type)

		sdBody.body.setUserData(entity)

		engine.addEntity(entity)
	}

	static float randomPos(float corner1, corner2) {
		Random rand = new Random()
		if(corner1 == corner2) {
			return corner1
		}
		float delta = corner2 - corner1 as float
		float offset = rand.nextFloat() * delta as float
		return corner1 + offset
	}
}
