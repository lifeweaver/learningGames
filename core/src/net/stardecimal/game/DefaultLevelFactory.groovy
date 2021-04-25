package net.stardecimal.game

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.utils.Array
import net.stardecimal.game.entity.components.SdBodyComponent
import net.stardecimal.game.entity.components.TextureComponent
import net.stardecimal.game.entity.components.TransformComponent
import net.stardecimal.game.entity.components.TypeComponent
import net.stardecimal.game.entity.systems.RenderingSystem
import net.stardecimal.game.loader.SdAssetManager

trait DefaultLevelFactory {
	BodyFactory bodyFactory
	World world
	PooledEngine engine
	SdAssetManager assetManager
	Entity player
	TextureRegion boundaryTex
	ParticleEffectManager pem

	void init(PooledEngine en, SdAssetManager am) {
		engine = en
		assetManager = am
		pem = new ParticleEffectManager()

		// the y is gravity, normal is -9.8f I think.
		world = new World(new Vector2(0, 0), true)
		world.setContactListener(new MyContactListener())
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

	void createBoundaries(boolean floor=true, boolean ceiling=true, boolean rightWall=true, boolean leftWall=true) {
		Vector2 screenSize = RenderingSystem.getScreenSizeInMeters()
		float boundaryWidth = 0.1f

		if(floor) {
			createBoundary(new Vector2(screenSize.x, 0.1f), screenSize.x, boundaryWidth)
		}

		if(ceiling) {
			createBoundary(new Vector2(screenSize.x, screenSize.y * 2 as float), screenSize.x, boundaryWidth)
		}

		if(rightWall) {
			createBoundary(new Vector2(screenSize.x * 2 as float, screenSize.y * 2 as float), boundaryWidth, screenSize.y)
		}

		if(leftWall) {
			createBoundary(new Vector2(0, 0), boundaryWidth, screenSize.y)
		}
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