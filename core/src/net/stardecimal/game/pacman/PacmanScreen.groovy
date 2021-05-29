package net.stardecimal.game.pacman

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.GL20
import net.stardecimal.game.GameScreen
import net.stardecimal.game.MyGames

class PacmanScreen extends ScreenAdapter implements GameScreen {
	LevelFactory levelFactory

	//TODO:
	//pacman
	//map collision using the properties.
	//pellet
	//power pellet
	//ghost
	//fruit


	PacmanScreen(final MyGames game) {
		init(game, LevelFactory.class, RenderingConstants.class)
		levelFactory = (LevelFactory) lvlFactory
		levelFactory.gameName = 'pacman'

		//28 wide
		//36 total high
		//31 high - game map
		//8x8 squares
		//2x2 pellet dot in middle
		//8x8 power pellet
		//each corridor should be 16x16, so two blocks

		//Ghosts 14x14?
		//pacman 13x13?

//		engine.addSystem(new PlayerControlSystem(levelFactory))
//		engine.addSystem(new CollisionSystem(parent, levelFactory))
//		engine.addSystem(new EnemySystem(levelFactory))

//		levelFactory.createBoundaries()
//		levelFactory.createPlayer(camera)
//		levelFactory.createEnemies()
//		levelFactory.createShields()
//		levelFactory.playerLives = 3

//		parent.recorder = new GifRecorder(batch)
	}

	void resetWorld() {
		reset()
		levelFactory.playerScore = 0
		levelFactory.enemyScore = 0
		levelFactory.playerLives = 3
//		levelFactory.createBoundaries()
//		levelFactory.createPlayer(camera)
//		levelFactory.createEnemies()
//		levelFactory.createShields()
	}


	@Override
	void render(float delta) {
		if(parent.state == MyGames.STATE.RUNNING) {
			Gdx.gl.glClearColor(0,0,0, 1)
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
			engine.update(delta)

			//Move to end game screen once all lives used up
			if(levelFactory.playerLives == -1) {
				parent.changeScreen(parent.ENDGAME)
			}

			levelFactory.hud.setScore(levelFactory.playerScore)
			levelFactory.hud.setLives(levelFactory.playerLives)
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
