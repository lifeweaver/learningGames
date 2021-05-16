package net.stardecimal.game.asteroids

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.GL20
import net.stardecimal.game.GameScreen
import net.stardecimal.game.MyGames
import net.stardecimal.game.asteroids.entity.systems.AsteroidSpawningSystem
import net.stardecimal.game.asteroids.entity.systems.BulletSystem
import net.stardecimal.game.asteroids.entity.systems.CollisionSystem
import net.stardecimal.game.asteroids.entity.systems.EnemySystem
import net.stardecimal.game.asteroids.entity.systems.PlayerControlSystem
import net.stardecimal.game.asteroids.entity.systems.SpaceSystem

class AsteroidsScreen extends ScreenAdapter implements GameScreen {
	LevelFactory levelFactory

	AsteroidsScreen(final MyGames game) {
		init(game, LevelFactory.class)
		levelFactory = (LevelFactory) lvlFactory

		engine.addSystem(new PlayerControlSystem(controller, levelFactory))
		engine.addSystem(new CollisionSystem(parent, levelFactory))
		engine.addSystem(new SpaceSystem())
		engine.addSystem(new BulletSystem(parent))
		engine.addSystem(new EnemySystem(levelFactory))
		engine.addSystem(new AsteroidSpawningSystem(levelFactory, 5))
		levelFactory.createPlayer(camera)
		levelFactory.playerLives = 3
	}

	void resetWorld() {
		reset()
		levelFactory.createPlayer(camera)
		levelFactory.playerScore = 0
		levelFactory.playerLives = 3
	}

	@Override
	void render(float delta) {
		if(parent.state == MyGames.STATE.RUNNING) {

			//Clear screen to black
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
