package net.stardecimal.game.missilecommand

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.GL20
import net.stardecimal.game.GameScreen
import net.stardecimal.game.MyGames
import net.stardecimal.game.missilecommand.entity.systems.CollisionSystem
import net.stardecimal.game.missilecommand.entity.systems.EnemyFiringSystem
import net.stardecimal.game.missilecommand.entity.systems.EnemySpawningSystem
import net.stardecimal.game.missilecommand.entity.systems.MissileSystem
import net.stardecimal.game.missilecommand.entity.systems.PlayerControlSystem

class MissileCommandScreen extends ScreenAdapter implements GameScreen {
	LevelFactory levelFactory
	float initialEnemyMissileSpawnInterval = 5f

	MissileCommandScreen(final MyGames game) {
		init(game, LevelFactory.class)
		levelFactory = (LevelFactory) lvlFactory

		engine.addSystem(new PlayerControlSystem(controller, levelFactory, camera))
		engine.addSystem(new CollisionSystem(parent, levelFactory))
		engine.addSystem(new EnemySpawningSystem(levelFactory, initialEnemyMissileSpawnInterval))
		engine.addSystem(new EnemyFiringSystem(levelFactory))
		engine.addSystem(new MissileSystem(game, levelFactory))


		levelFactory.createCities()
		levelFactory.createDefenderMissiles()
		levelFactory.createGroundBarrier()
		levelFactory.createPlayer(camera)
		levelFactory.createCrosshair()
//		levelFactory.initScore()
		levelFactory.startMissileBarrage()
	}

	void resetWorld() {
		reset()
		parent.playerScore = 0
		parent.enemyScore = 0

		levelFactory.createCities()
		levelFactory.createDefenderMissiles()
		levelFactory.createPlayer(camera)
		levelFactory.startMissileBarrage()
//		levelFactory.initScore()
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
