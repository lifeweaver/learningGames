package com.stardecimal.game.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.math.Vector2
import com.stardecimal.game.GameJamGame
import com.stardecimal.game.LevelFactory
import com.stardecimal.game.entity.systems.CollisionSystem
import com.stardecimal.game.entity.systems.EnemySpawningSystem
import com.stardecimal.game.entity.systems.FallingBallSystem
import com.stardecimal.game.entity.systems.PlayerControlSystem
import com.stardecimal.game.entity.util.Mapper
import com.stardecimal.game.entity.components.SdBodyComponent
import com.stardecimal.game.entity.systems.RenderingSystem
import com.stardecimal.game.util.GameScreen

class MainGameScreen extends ScreenAdapter implements GameScreen {
	LevelFactory levelFactory
	RenderingSystem renderingSystem

	MainGameScreen(final GameJamGame game) {
		init(game, LevelFactory.class)
		levelFactory = (LevelFactory) lvlFactory
		levelFactory.world.gravity = new Vector2(0, -9.8f)
		levelFactory.gameName = 'Balls - a rolling story'
		levelFactory.parent = this

		engine.addSystem(new PlayerControlSystem(levelFactory, camera))
		engine.addSystem(new CollisionSystem(parent, levelFactory))
		engine.addSystem(new FallingBallSystem(levelFactory))
		engine.addSystem(new EnemySpawningSystem(levelFactory, 1))

		renderingSystem = engine.getSystem(RenderingSystem)


		resetWorld()
	}

	void resetWorld() {
		reset()
		levelFactory.playerScore = 1000
		levelFactory.enemyScore = 0
		levelFactory.playerLives = 3

		levelFactory.buildMap()
		levelFactory.createPlayer(camera)
		levelFactory.generateLevel(20)
	}

	@Override
	void render(float delta) {
		if(parent.state == GameJamGame.STATE.RUNNING) {
			Gdx.gl.glClearColor(0,0,0, 1)
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
			//Update the camera location before updating the systems.
			if(levelFactory.player) {
				SdBodyComponent playerBody = Mapper.bCom.get(levelFactory.player)
				if(playerBody.body.position.x > 20 / RenderingSystem.PPM) {
					renderingSystem.getCamera().position.x = playerBody.body.position.x
				}
			}
			engine.update(delta)
			levelFactory.hud.setScore(levelFactory.playerScore)
		} else if(parent.state == GameJamGame.STATE.OVER) {
			parent.changeScreen(GameJamGame.ENDGAME)
		}
	}

	@Override
	void hide() {
		super.hide()
	}

	@Override
	void show() {
		super.show()
		Gdx.input.setInputProcessor(parent.multiplexer)
	}

	@Override
	void dispose() {
		levelFactory.world.dispose()
		levelFactory.bodyFactory.dispose()
	}
}
