package net.stardecimal.game.missilecommand

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.GL20
import net.stardecimal.game.GameScreen
import net.stardecimal.game.MyGames
import net.stardecimal.game.missilecommand.entity.systems.CollisionSystem
import net.stardecimal.game.missilecommand.entity.systems.EnemySystem
import net.stardecimal.game.missilecommand.entity.systems.EnemySpawningSystem
import net.stardecimal.game.missilecommand.entity.systems.MissileSystem
import net.stardecimal.game.missilecommand.entity.systems.PlayerControlSystem

class MissileCommandScreen extends ScreenAdapter implements GameScreen {
	LevelFactory levelFactory
	float initialEnemyMissileSpawnInterval = 5f

	MissileCommandScreen(final MyGames game) {
		init(game, LevelFactory.class)
		levelFactory = (LevelFactory) lvlFactory

		engine.addSystem(new PlayerControlSystem(levelFactory, camera))
		engine.addSystem(new CollisionSystem(parent, levelFactory))
		engine.addSystem(new EnemySpawningSystem(levelFactory, initialEnemyMissileSpawnInterval))
		engine.addSystem(new EnemySystem(levelFactory))
		engine.addSystem(new MissileSystem(game, levelFactory))


		levelFactory.createCities()
		levelFactory.createDefenderMissiles()
		levelFactory.createGroundBarrier()
		levelFactory.createPlayer(camera)
		levelFactory.createCrosshair()
		levelFactory.startMissileBarrage()
	}

	void resetWorld() {
		reset()
		levelFactory.playerScore = 0
		levelFactory.enemyScore = 0
		levelFactory.missilesLeft = levelFactory.startMissiles

		levelFactory.createCities()
		levelFactory.createDefenderMissiles()
		levelFactory.createGroundBarrier()
		levelFactory.createPlayer(camera)
		levelFactory.createCrosshair()
		levelFactory.startMissileBarrage()
	}

	@Override
	void render(float delta) {
		if(parent.state == MyGames.STATE.RUNNING) {
			Gdx.gl.glClearColor(0,0,0, 1)
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

			engine.update(delta)
			//Score and move to end game screen
			if(!levelFactory.missilesLeft && !levelFactory.missilesInFlight()) {
				levelFactory.calculateScore()
				parent.changeScreen(parent.ENDGAME)
			}
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
