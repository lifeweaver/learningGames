package net.stardecimal.game

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.Screen
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.Box2D
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.utils.Array

class PongGame extends Game {
	SpriteBatch batch
	BitmapFont font
	World world
	float accumulator = 0
	Array<Body> bodies
	Array<Screen> screens

	@Override
	void create () {
		Box2D.init()
		screens= new Array<Screen>()
		batch = new SpriteBatch()
		font = new BitmapFont()
		world = new World(new Vector2(0,0), true)
//		Box2DDebugRenderer debugRenderer = new Box2DDebugRenderer()
		bodies = new Array<Body>()

		// add bodies

		// add bodies to world
		world.getBodies(bodies)


		this.setScreen(new MainMenuScreen(this))
	}

	@Override
	void setScreen(Screen screen) {
		super.setScreen(screen)
		screens.add(screen)
	}

	@Override
	void render () {
		super.render()
		world.getBodies(bodies)
		// update bodies
		bodies.each {Body body ->
			def e = body.getUserData()
			if(e) {
//				// Update the entities/sprites position and angle
//				e.setPosition(b.position.x, body.position.y)
//				// We need to convert our angle from radians to degrees
//				e.setRotation(MathUtils.radiansToDegrees * body.angle)
			}
		}

		world.step(1/45f as float, 6, 2)
	}

	private void doPhysicsStep(float deltaTime) {
//		 fixed time step
//		 max frame time to avoid spiral of death (on slow devices)
		float frameTime = Math.min(deltaTime, 0.25f)
		accumulator += frameTime
//		while (accumulator >= Constants.TIME_STEP) {
//			WorldManager.world.step(Constants.TIME_STEP, Constants.VELOCITY_ITERATIONS, Constants.POSITION_ITERATIONS)
//			accumulator -= Constants.TIME_STEP
//		}
	}

	@Override
	void dispose () {
		screens.each {
			it.dispose()
		}
	}
}
