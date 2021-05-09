package net.stardecimal.game.spaceinvaders

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.GL20
import net.stardecimal.game.GameScreen
import net.stardecimal.game.MyGames
import net.stardecimal.game.spaceinvaders.entity.systems.PlayerControlSystem

class SpaceInvadersScreen extends ScreenAdapter implements GameScreen {
	LevelFactory levelFactory

	SpaceInvadersScreen(final MyGames game) {
		init(game, LevelFactory.class)
		levelFactory = (LevelFactory) lvlFactory

		engine.addSystem(new PlayerControlSystem(controller, levelFactory))
//		engine.addSystem(new CollisionSystem(parent, levelFactory))
//		levelFactory.createGroundBarrier()
		levelFactory.createPlayer(camera)
		levelFactory.createBoundaries()

	}

	void resetWorld() {
		reset()
		levelFactory.playerScore = 0
		levelFactory.enemyScore = 0
		//TODO: copy anything needed from the constructor
	}

	@Override
	void render(float delta) {
		if(parent.state == MyGames.STATE.RUNNING) {
			Gdx.gl.glClearColor(0,0,0, 1)
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

			engine.update(delta)
			//Score and move to end game screen
//			if(!levelFactory.missilesLeft && !levelFactory.missilesInFlight()) {
//				levelFactory.calculateScore()
//				parent.changeScreen(parent.ENDGAME)
//			}
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
