package net.stardecimal.game.worm


import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.GL20
import net.stardecimal.game.GameScreen
import net.stardecimal.game.MyGames
import net.stardecimal.game.entity.systems.RenderingSystem
import net.stardecimal.game.worm.entity.systems.CollisionSystem
import net.stardecimal.game.worm.entity.systems.PlayerControlSystem

class WormScreen extends ScreenAdapter implements GameScreen {
	LevelFactory levelFactory

	WormScreen(final MyGames game) {
		init(game, LevelFactory.class)
		levelFactory = (LevelFactory) lvlFactory
		levelFactory.gameName = 'worm'

		engine.addSystem(new PlayerControlSystem(levelFactory))
		engine.addSystem(new CollisionSystem(parent, levelFactory))
		engine.getSystem(RenderingSystem).addTiledMapBackground(levelFactory.generateBackground())

		player = levelFactory.createPlayer(camera)
		levelFactory.createBoundaries()
		levelFactory.createFruit()
	}

	void resetWorld() {
		reset()
		levelFactory.playerScore = 0
		levelFactory.enemyScore = 0

		player = levelFactory.createPlayer(camera)
		levelFactory.createBoundaries()
		levelFactory.createFruit()
	}

	@Override
	void render(float delta) {
		if(parent.state == MyGames.STATE.RUNNING) {
			Gdx.gl.glClearColor(0, 0, 0, 1)
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

			engine.update(delta)
		}
	}

	@Override
	void show() {
		Gdx.input.setInputProcessor(parent.multiplexer)
	}

	@Override
	void dispose() {
		levelFactory.world.dispose()
		levelFactory.bodyFactory.dispose()
	}
}
