package net.stardecimal.game.pong

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.GL20
import net.stardecimal.game.GameScreen
import net.stardecimal.game.MyGames
import net.stardecimal.game.pong.entity.systems.PingPongSystem
import net.stardecimal.game.pong.entity.systems.PongCollisionSystem
import net.stardecimal.game.pong.entity.systems.PongPaddleEnemySystem
import net.stardecimal.game.pong.entity.systems.PongPlayerControlSystem


class PongScreen extends ScreenAdapter implements GameScreen {

	PongScreen(final MyGames game) {
		init(game)

		engine.addSystem(new PongPlayerControlSystem(controller))
		engine.addSystem(new PongCollisionSystem(parent, levelFactory))
		engine.addSystem(new PongPaddleEnemySystem(levelFactory))
		engine.addSystem(new PingPongSystem(parent, levelFactory))

		player = levelFactory.createPlayer(camera)
		levelFactory.createPingPong()
		levelFactory.createEnemy()
		levelFactory.createFloor()
		levelFactory.createCeiling()
		levelFactory.createEnemyScoringWall()
	}

	void resetWorld() {
		reset()
		parent.playerScore = 0
		parent.enemyScore = 0

		player = levelFactory.createPlayer(camera)
		levelFactory.createEnemy()
		levelFactory.createPingPong()
		levelFactory.createFloor()
		levelFactory.createCeiling()
		levelFactory.createEnemyScoringWall()
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
