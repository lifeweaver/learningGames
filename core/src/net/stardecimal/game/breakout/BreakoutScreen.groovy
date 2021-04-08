package net.stardecimal.game.breakout

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.GL20
import net.stardecimal.game.GameScreen
import net.stardecimal.game.MyGames
import net.stardecimal.game.breakout.entity.system.CollisionSystem
import net.stardecimal.game.breakout.entity.system.PingPongSystem
import net.stardecimal.game.breakout.entity.system.PlayerControlSystem

class BreakoutScreen extends ScreenAdapter implements GameScreen {
	LevelFactory levelFactory

	BreakoutScreen(final MyGames game) {
		init(game, LevelFactory.class)
		levelFactory = (LevelFactory) lvlFactory

		engine.addSystem(new PlayerControlSystem(controller))
		engine.addSystem(new CollisionSystem(parent, levelFactory))
		engine.addSystem(new PingPongSystem(parent, levelFactory))

		player = levelFactory.createPlayer(camera)
		levelFactory.createBoundaries(false)
		levelFactory.createPingPong()
		levelFactory.createBoxes()
	}

	void resetWorld() {
		reset()
		parent.playerScore = 0
		parent.enemyScore = 0

		player = levelFactory.createPlayer(camera)
		levelFactory.createBoundaries()
		levelFactory.createPingPong()
		levelFactory.createBoxes()
	}

	@Override
	void render(float delta) {
		Gdx.gl.glClearColor(0,0,0, 1)
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

		engine.update(delta)
	}

	@Override
	void show() {
		Gdx.input.setInputProcessor(controller)
	}
}
