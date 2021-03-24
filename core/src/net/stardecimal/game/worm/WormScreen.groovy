package net.stardecimal.game.worm


import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.GL20
import net.stardecimal.game.GameScreen
import net.stardecimal.game.MyGames
import net.stardecimal.game.worm.entity.systems.PlayerControlSystem

class WormScreen extends ScreenAdapter implements GameScreen {
	LevelFactory levelFactory

	WormScreen(final MyGames game) {
		init(game, LevelFactory.class)
		levelFactory = (LevelFactory) lvlFactory

		engine.addSystem(new PlayerControlSystem(controller))
//		engine.addSystem(new PingPongCollisionSystem(parent, levelFactory))
//		engine.addSystem(new PongPaddleEnemySystem(levelFactory))
//		engine.addSystem(new PingPongSystem(parent, levelFactory))

		player = levelFactory.createPlayer(camera)
		levelFactory.createBoundaries()
//		levelFactory.createPingPong()
//		levelFactory.createEnemy()
//		levelFactory.createFloor()
//		levelFactory.createCeiling()
//		levelFactory.createEnemyScoringWall()
	}

	void resetWorld() {
		reset()
		parent.playerScore = 0
		parent.enemyScore = 0

//		player = levelFactory.createPlayer(camera)
//		levelFactory.createEnemy()
//		levelFactory.createPingPong()
//		levelFactory.createFloor()
//		levelFactory.createCeiling()
//		levelFactory.createEnemyScoringWall()
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
