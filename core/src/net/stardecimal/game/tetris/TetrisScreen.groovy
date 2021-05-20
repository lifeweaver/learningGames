package net.stardecimal.game.tetris

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.GL20
import net.stardecimal.game.GameScreen
import net.stardecimal.game.MyGames

class TetrisScreen extends ScreenAdapter implements GameScreen {
	LevelFactory levelFactory

	TetrisScreen(final MyGames game) {
		init(game, LevelFactory.class)
		levelFactory = (LevelFactory) lvlFactory
		levelFactory.gameName = 'tetris'

//		engine.addSystem(new PlayerControlSystem(levelFactory))
		// engine.addSystem(new BlockSpawningSystem(levelFactory))
		levelFactory.createPlayer(camera)
		levelFactory.playerLives = 0
	}

	void resetWorld() {
		reset()
		levelFactory.createPlayer(camera)
		levelFactory.playerScore = 0
		levelFactory.playerLives = 0
	}

	@Override
	void render(float delta) {
		if(parent.state == MyGames.STATE.RUNNING) {

			//Clear screen to black
			Gdx.gl.glClearColor(0,0,0, 1)
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
			engine.update(delta)

			//Move to end game screen once all lives used up
			if(levelFactory.playerLives > 0) {
				parent.changeScreen(parent.ENDGAME)
			}

			if(levelFactory.hud) {
				levelFactory.hud.setScore(levelFactory.playerScore)
				levelFactory.hud.setLives(levelFactory.playerLives)
			}
		}

		// Gif Recorder support
		if(parent.recorder) {
			parent.recorder.update()
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
