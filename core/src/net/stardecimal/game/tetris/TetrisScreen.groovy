package net.stardecimal.game.tetris

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.math.Vector2
import net.stardecimal.game.GameScreen
import net.stardecimal.game.MyGames
import net.stardecimal.game.tetris.entity.systems.MovementSystem
import net.stardecimal.game.tetris.entity.systems.PlayerControlSystem

class TetrisScreen extends ScreenAdapter implements GameScreen {
	LevelFactory levelFactory

	//TODO:
	//move to top center
	//if a new block spawns on an existing one, game over
	//look at the random bag thing for choosing the pieces
	//clear lines
	//update score
	//show preview block
	//show level

	TetrisScreen(final MyGames game) {
		init(game, LevelFactory.class)
		levelFactory = (LevelFactory) lvlFactory
		levelFactory.gameName = 'tetris'

		engine.addSystem(new PlayerControlSystem(levelFactory, camera))
		engine.addSystem(new MovementSystem(levelFactory))
		// engine.addSystem(new BlockSpawningSystem(levelFactory))

		levelFactory.playerLives = 0
		levelFactory.spawnRandomBlock()
	}

	void resetWorld() {
		reset()
		levelFactory.createBoundaries(true, false)
		levelFactory.grid = levelFactory.generateCleanGrid()
		levelFactory.spawnRandomBlock()
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

		levelFactory.drawDebugLine(new Vector2(0, 0), new Vector2(0, 18), camera.combined)
		levelFactory.drawDebugLine(new Vector2(0, 18), new Vector2(10, 18), camera.combined)
		levelFactory.drawDebugLine(new Vector2(10, 18), new Vector2(10, 0), camera.combined)
		levelFactory.drawDebugLine(new Vector2(10, 0), new Vector2(0, 0), camera.combined)
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
