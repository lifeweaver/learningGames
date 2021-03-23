package net.stardecimal.game.screens

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import net.stardecimal.game.GameScreen
import net.stardecimal.game.KeyboardController
import net.stardecimal.game.MyGames
import net.stardecimal.game.WormFactory
import net.stardecimal.game.entity.systems.PhysicsDebugSystem
import net.stardecimal.game.entity.systems.PhysicsSystem
import net.stardecimal.game.entity.systems.PongPlayerControlSystem
import net.stardecimal.game.entity.systems.RenderingSystem
import net.stardecimal.game.entity.systems.SteeringSystem

class WormScreen extends ScreenAdapter implements GameScreen {

	WormScreen(final MyGames game) {
		init(game)

//		engine.addSystem(new PingPongCollisionSystem(parent, levelFactory))
//		engine.addSystem(new PongPaddleEnemySystem(levelFactory))
//		engine.addSystem(new PingPongSystem(parent, levelFactory))

//		player = levelFactory.createPlayer(camera)
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
