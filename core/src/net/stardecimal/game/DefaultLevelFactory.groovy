package net.stardecimal.game

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.utils.Array
import net.stardecimal.game.entity.components.ParticleEffectComponent
import net.stardecimal.game.entity.components.SdBodyComponent
import net.stardecimal.game.entity.components.TextureComponent
import net.stardecimal.game.entity.components.TransformComponent
import net.stardecimal.game.entity.components.TypeComponent
import net.stardecimal.game.entity.systems.RenderingSystem
import net.stardecimal.game.loader.SdAssetManager
import org.slf4j.Logger
import org.slf4j.LoggerFactory

trait DefaultLevelFactory {
	static final Logger log = LoggerFactory.getLogger(DefaultLevelFactory)
	static ShapeRenderer shapeRenderer = new ShapeRenderer()
	BodyFactory bodyFactory
	World world
	PooledEngine engine
	SdAssetManager assetManager
	Entity player
	TextureRegion boundaryTex
	ParticleEffectManager pem
	KeyboardController controller
	String gameName = 'default'
	TiledMap map
	int playerScore = 0
	int enemyScore = 0
	int playerLives = 0
	def hud
	def customContactListener

	void init(PooledEngine en, SdAssetManager am) {
		engine = en
		assetManager = am
		pem = new ParticleEffectManager()

		// the y is gravity, normal is -9.8f I think.
		world = new World(new Vector2(0, 0), true)
		if(customContactListener) {
			world.setContactListener(customContactListener)
		} else {
			world.setContactListener(new MyContactListener())
		}
		bodyFactory = BodyFactory.getInstance(world)
		boundaryTex = DFUtils.makeTextureRegion(RenderingSystem.getScreenSizeInMeters().x / RenderingSystem.PPM as float, 0.1f, '#ffffff')
	}

	void resetWorld() {
		Array<Body> bodies = new Array<>()
		world.getBodies(bodies)
		bodies.each {
			world.destroyBody(it)
		}
	}

	void createScrollingYBoundaries(float totalHeight) {
		createBoundary(new Vector2(0, totalHeight), 0.1, totalHeight)
		createBoundary(new Vector2(RenderingSystem.screenSizeInPixesWorld.x * 2 as float, totalHeight), 0.1, totalHeight)
		createBoundary(new Vector2(RenderingSystem.screenSizeInPixesWorld.x, 0.1f), RenderingSystem.screenSizeInPixesWorld.x, 0.1)
	}

	void createBoundaries(boolean floor=true, boolean ceiling=true, boolean rightWall=true, boolean leftWall=true) {
		Vector2 screenSize = RenderingSystem.getScreenSizeInMeters()
		float boundaryWidth = 0.1f

		if(floor) {
			createBoundary(new Vector2(screenSize.x, 0.1f), screenSize.x, boundaryWidth, RenderingSystem.PPM)
		}

		if(ceiling) {
			createBoundary(new Vector2(screenSize.x, screenSize.y * 2 as float), screenSize.x, boundaryWidth, RenderingSystem.PPM)
		}

		if(rightWall) {
			createBoundary(new Vector2(screenSize.x * 2 as float, screenSize.y * 2 as float), boundaryWidth, screenSize.y, RenderingSystem.PPM)
		}

		if(leftWall) {
			createBoundary(new Vector2(0, 0), boundaryWidth, screenSize.y, RenderingSystem.PPM)
		}
	}

	void createBoundary(Vector2 pos, float width, float height, float ppm=1) {
		Entity entity = engine.createEntity()
		SdBodyComponent sdBody = engine.createComponent(SdBodyComponent)
		TransformComponent position = engine.createComponent(TransformComponent)
		TextureComponent texture = engine.createComponent(TextureComponent)
		TypeComponent type = engine.createComponent(TypeComponent)

		//Divide by the PPM then by 2 unless they are zero
		float x = pos.x != 0 ? pos.x / ppm / 2 as float : pos.x
		float y = pos.y != 0 ? pos.y / ppm / 2 as float : pos.y

		position.position.set(x, y, 0)
		texture.region = boundaryTex
		type.type = TypeComponent.TYPES.SCENERY
		sdBody.body = bodyFactory.makeBoxPolyBody(x, y, width, height, BodyFactory.STONE, BodyDef.BodyType.StaticBody)

		entity.add(sdBody)
		entity.add(texture)
		entity.add(position)
		entity.add(type)

		sdBody.body.setUserData(entity)

		engine.addEntity(entity)
	}

	abstract TiledMap generateBackground()
	abstract def createHud(SpriteBatch batch)

	static float randomPos(float corner1, corner2) {
		Random rand = new Random()
		if(corner1 == corner2) {
			return corner1
		}
		float delta = corner2 - corner1 as float
		float offset = rand.nextFloat() * delta as float
		return corner1 + offset
	}


	/**
	 * Make particle effect at xy
	 * @param x
	 * @param y
	 * @param CollisionComponent
	 * @return the Particle Effect Entity
	 */
	Entity makeParticleEffect(int type, float x, float y, boolean killOnParentBodyDeath=false) {
		Entity entPE = engine.createEntity()
		ParticleEffectComponent pec = engine.createComponent(ParticleEffectComponent.class)
		pec.particleEffect = pem.getPooledParticleEffect(type)
		pec.particleEffect.setPosition(x, y)
		pec.killOnParentBodyDeath = killOnParentBodyDeath
		entPE.add(pec)
		engine.addEntity(entPE)
		return entPE
	}

	/**
	 * Attache particle effect to body from body component with offsets
	 * @param type the type of particle effect to show
	 * @param sdBody the bodycomponent with the body to attach to
	 * @param xo x offset
	 * @param yo y offset
	 * @return the Particle Effect Entity
	 */
	Entity makeParticleEffect(int type, SdBodyComponent sdBody, float xo=0, float yo=0, boolean killOnParentBodyDeath=false) {
		Entity entPE = engine.createEntity()
		ParticleEffectComponent pec = engine.createComponent(ParticleEffectComponent.class)
		pec.particleEffect = pem.getPooledParticleEffect(type)
		pec.particleEffect.setPosition(sdBody.body.position.x, sdBody.body.position.y)
		pec.xOffset = xo
		pec.yOffset = yo
		pec.isAttached = true
		pec.attachedBody = sdBody.body
		pec.killOnParentBodyDeath = killOnParentBodyDeath
		entPE.add(pec)
		engine.addEntity(entPE)
		return entPE
	}

	Array<Body> circleRayCast(Vector2 center, float radius, int rayCount=30) {
		Array<SeeThroughRayCastCallback> rayCasts = new Array<SeeThroughRayCastCallback>()
		Array<Body> rayCastBodies = new Array<Body>()
		Vector2 direction = new Vector2()

		//Start with zero degrees
		direction.set(1, 0)
		float rotateAngle = 360 / rayCount

		rayCount.times {
			float x1 = center.x
			float y1 = center.y
			float x2 = x1 + direction.x as float
			float y2 = y1 + direction.y as float
			float newRadius = radius as float
			Vector2 endPoint = new Vector2(x2, y2)
					.sub(x1, y1)
					.nor()
					.scl(newRadius)
					.add(x1, y1)

			SeeThroughRayCastCallback ray = new SeeThroughRayCastCallback(rayCastBodies, endPoint)
			world.rayCast(ray, new Vector2(x1, y1), new Vector2(endPoint))
			rayCasts.add(ray)
			direction.rotateDeg(rotateAngle)
		}

//		if(rayCasts && !rayCasts.first().collisionBodies.isEmpty()) {
//			log.debug("rayCasts: ${rayCasts}, jtest: ${Integer.toHexString(rayCasts.first().collisionBodies.first().hashCode())}")
//		}
//		if(rayCastBodies && !rayCastBodies.isEmpty()) {
//			log.debug("rayCastBodies: ${rayCastBodies}, jtest2: ${Integer.toHexString(rayCastBodies.first().hashCode())}")
//		}

		return rayCastBodies
	}

	Array<Body> singleRayCast(Vector2 start, Vector2 end) {
		Array<Body> rayCastBodies = new Array<Body>()
		SeeThroughRayCastCallback ray = new SeeThroughRayCastCallback(rayCastBodies, end)
		world.rayCast(ray, new Vector2(start), new Vector2(end))

		return rayCastBodies
	}

	Array<Body> aabb(Vector2 lower, Vector2 upper) {
		AllAABBQueryCallback aabbQueryCallback = new AllAABBQueryCallback()
		world.QueryAABB(aabbQueryCallback, lower.x, lower.y, upper.x, upper.y)

		return aabbQueryCallback.collisionBodies
	}

//	https://stackoverflow.com/a/30781020/2137125
	static void drawDebugLine(Vector2 start, Vector2 end, int lineWidth, Color color, Matrix4 projectionMatrix) {
		Gdx.gl.glLineWidth(lineWidth)
		shapeRenderer.setProjectionMatrix(projectionMatrix)
		shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
		shapeRenderer.setColor(color)
		shapeRenderer.line(start, end)
		shapeRenderer.end()
		Gdx.gl.glLineWidth(1)
	}

	static void drawDebugLine(Vector2 start, Vector2 end, Matrix4 projectionMatrix, float width=2) {
		Gdx.gl.glLineWidth(width)
		shapeRenderer.setProjectionMatrix(projectionMatrix)
		shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
		shapeRenderer.setColor(Color.WHITE)
		shapeRenderer.line(start, end)
		shapeRenderer.end()
		Gdx.gl.glLineWidth(1)
	}

	static void  drawGrid(lvlFactory, Matrix4 combined) {
		float tileWidth, tileHeight, offsetX, width, height
		Vector2 screenSize
		if(lvlFactory?.map) {
			TiledMapTileLayer collisionLayer = (TiledMapTileLayer) lvlFactory.map.layers.first()
			tileWidth = collisionLayer.tileWidth * RenderingSystem.PIXELS_TO_METRES as float
			tileHeight = collisionLayer.tileHeight * RenderingSystem.PIXELS_TO_METRES as float
			offsetX = collisionLayer.offsetX / (1 / RenderingSystem.PIXELS_TO_METRES)
			screenSize = RenderingSystem.getScreenSizeInPixesWorld()
			width = collisionLayer.width + 1
			height = collisionLayer.height
		} else {
			tileWidth = 1
			tileHeight = 1
			offsetX = 0
			screenSize = RenderingSystem.getScreenSizeInMeters()
			width = screenSize.x
			height = screenSize.y
		}

		width.times {
			float x = offsetX + it * tileWidth as float
			lvlFactory.drawDebugLine(new Vector2(x, 0), new Vector2(x, screenSize.y), combined, 0.1)
		}

		height.times {
			float y = it * tileHeight as float
			lvlFactory.drawDebugLine(new Vector2(offsetX, y), new Vector2(offsetX + (width - 1) * tileWidth as float, y), combined, 0.1)
		}
	}

}