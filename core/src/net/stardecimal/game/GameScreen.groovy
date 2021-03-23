package net.stardecimal.game

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import net.stardecimal.game.entity.systems.PhysicsDebugSystem
import net.stardecimal.game.entity.systems.PhysicsSystem
import net.stardecimal.game.entity.systems.RenderingSystem
import net.stardecimal.game.entity.systems.SteeringSystem
import net.stardecimal.game.pong.PongFactory

trait GameScreen {
	MyGames parent
	OrthographicCamera camera
	KeyboardController controller
	SpriteBatch batch
	PooledEngine engine
	Entity player
	LevelFactory lvlFactory

	void init(final MyGames game, Class instance) {
		this.parent = game

		parent.assetManager.queueAddSounds()
		parent.assetManager.manager.finishLoading()
		controller = new KeyboardController()
		engine = new PooledEngine()
		lvlFactory = (LevelFactory) instance.newInstance(engine, parent.assetManager)

		batch = new SpriteBatch()
		RenderingSystem renderingSystem = new RenderingSystem(batch)
		camera = renderingSystem.camera
		batch.projectionMatrix = camera.combined

		engine.addSystem(new PhysicsSystem(lvlFactory.world))
		engine.addSystem(renderingSystem)
		engine.addSystem(new PhysicsDebugSystem(lvlFactory.world, renderingSystem.camera))
		engine.addSystem(new SteeringSystem())
	}

	void reset() {
		println('Resetting world')
		engine.removeAllEntities()
		levelFactory.resetWorld()

		// reset controller controls (fixes bug where controller stuck on direction if died in that position)
		controller.left = false
		controller.right = false
		controller.up = false
		controller.down = false
		controller.isMouse1Down = false
		controller.isMouse2Down = false
		controller.isMouse3Down = false
	}
}