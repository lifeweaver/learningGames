package net.stardecimal.game

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import net.stardecimal.game.entity.systems.ParticleEffectSystem
import net.stardecimal.game.entity.systems.PhysicsDebugSystem
import net.stardecimal.game.entity.systems.PhysicsSystem
import net.stardecimal.game.entity.systems.RenderingSystem
import net.stardecimal.game.entity.systems.SteeringSystem

trait GameScreen {
	MyGames parent
	OrthographicCamera camera
	SpriteBatch batch
	PooledEngine engine
	Entity player
	DefaultLevelFactory lvlFactory

	void init(final MyGames game, Class leveFactoryClass, renderingConstants=null) {
		this.parent = game

		parent.assetManager.queueAddSounds()
		parent.assetManager.queueAddImages()
		parent.assetManager.queueAddIndividualAssets()
		parent.assetManager.manager.finishLoading()
		engine = new PooledEngine()
		lvlFactory = (DefaultLevelFactory) leveFactoryClass.newInstance(engine, parent.assetManager)
		lvlFactory.controller = new KeyboardController()
		parent.multiplexer.addProcessor(lvlFactory.controller)

		batch = new SpriteBatch()
		RenderingSystem renderingSystem = new RenderingSystem(batch, renderingConstants)
		renderingSystem.addTiledMapBackground(lvlFactory.generateBackground())
		renderingSystem.addHud(lvlFactory.createHud(batch))
		camera = renderingSystem.camera
		engine.addSystem(new ParticleEffectSystem(batch, camera, lvlFactory.world))
		batch.projectionMatrix = camera.combined

		engine.addSystem(new PhysicsSystem(lvlFactory.world))
		engine.addSystem(renderingSystem)

		if(System.getenv('debug') == 'true') {
			engine.addSystem(new PhysicsDebugSystem(lvlFactory.world, renderingSystem.camera))
		}
		engine.addSystem(new SteeringSystem())
	}

	void reset() {
		println('Resetting world')
		engine.removeAllEntities()
		lvlFactory.resetWorld()

		// reset controller controls (fixes bug where controller stuck on direction if died in that position)
		lvlFactory.controller.reset()
	}
}