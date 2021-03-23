package net.stardecimal.game.screens

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import net.stardecimal.game.KeyboardController
import net.stardecimal.game.PongFactory
import net.stardecimal.game.MyGames
import net.stardecimal.game.entity.systems.PingPongSystem
import net.stardecimal.game.entity.systems.PingPongCollisionSystem
import net.stardecimal.game.entity.systems.PongPaddleEnemySystem
import net.stardecimal.game.entity.systems.PhysicsDebugSystem
import net.stardecimal.game.entity.systems.PhysicsSystem
import net.stardecimal.game.entity.systems.PlayerControlSystem
import net.stardecimal.game.entity.systems.RenderingSystem
import net.stardecimal.game.entity.systems.SteeringSystem

class PongScreen extends ScreenAdapter {
	final MyGames parent
	OrthographicCamera camera
	KeyboardController controller
	SpriteBatch batch
	PooledEngine engine
	Entity player
	PongFactory levelFactory

	PongScreen(final MyGames game) {
		this.parent = game

		parent.assetManager.queueAddSounds()
		parent.assetManager.manager.finishLoading()
		controller = new KeyboardController()
		engine = new PooledEngine()
		levelFactory = new PongFactory(engine, parent.assetManager)

		batch = new SpriteBatch()
		RenderingSystem renderingSystem = new RenderingSystem(batch)
		camera = renderingSystem.camera
		batch.projectionMatrix = camera.combined

		engine.addSystem(new PhysicsSystem(levelFactory.world))
		engine.addSystem(renderingSystem)
		engine.addSystem(new PhysicsDebugSystem(levelFactory.world, renderingSystem.camera))
		engine.addSystem(new PlayerControlSystem(controller))

		player = levelFactory.createPlayer(camera)
		levelFactory.createPingPong()
		levelFactory.createEnemy()

		engine.addSystem(new PingPongCollisionSystem(parent, levelFactory))
		engine.addSystem(new PongPaddleEnemySystem(levelFactory))
		engine.addSystem(new PingPongSystem(parent, levelFactory))
		engine.addSystem(new SteeringSystem())

		levelFactory.createFloor()
		levelFactory.createCeiling()
		levelFactory.createEnemyScoringWall()
	}

	void resetWorld() {
		println('Resetting world')
		engine.removeAllEntities()
		levelFactory.resetWorld()
		parent.playerScore = 0
		parent.enemyScore = 0

		player = levelFactory.createPlayer(camera)
		levelFactory.createEnemy()
		levelFactory.createPingPong()
		levelFactory.createFloor()
		levelFactory.createCeiling()
		levelFactory.createEnemyScoringWall()

		// reset controller controls (fixes bug where controller stuck on direction if died in that position)
		controller.left = false
		controller.right = false
		controller.up = false
		controller.down = false
		controller.isMouse1Down = false
		controller.isMouse2Down = false
		controller.isMouse3Down = false
	}

	@Override
	void render(float delta) {
		Gdx.gl.glClearColor(0,0,0, 1)
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

		engine.update(delta)
//		println(Gdx.graphics.framesPerSecond)

//		parent.playerScore =
//		parent.changeScreen(PongGame.)
	}

	@Override
	void show() {
		Gdx.input.setInputProcessor(controller)
	}
}
