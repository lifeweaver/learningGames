package com.stardecimal.game.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.math.Vector2
import com.stardecimal.game.GameJamGame
import com.stardecimal.game.LevelFactory
import com.stardecimal.game.entity.systems.CollisionSystem
import com.stardecimal.game.entity.systems.PlayerControlSystem
import com.stardecimal.game.entity.util.Mapper
import com.stardecimal.game.entity.components.SdBodyComponent
import com.stardecimal.game.entity.systems.RenderingSystem
import com.stardecimal.game.util.GameScreen

class MainGameScreen extends ScreenAdapter implements GameScreen {
	LevelFactory levelFactory
	RenderingSystem renderingSystem


	//TODO: thoughtful pancake easter egg

	MainGameScreen(final GameJamGame game) {
		init(game, LevelFactory.class)
		levelFactory = (LevelFactory) lvlFactory
		levelFactory.world.gravity = new Vector2(0, -9.8f)
		levelFactory.gameName = 'Balls - a rolling story'

		engine.addSystem(new PlayerControlSystem(levelFactory))
		engine.addSystem(new CollisionSystem(parent, levelFactory))

		renderingSystem = engine.getSystem(RenderingSystem)


		resetWorld()
	}

	void resetWorld() {
		reset()
		levelFactory.playerScore = 0
		levelFactory.enemyScore = 0
		levelFactory.playerLives = 3
//		float totalHeight = (levelFactory.collisionLayer.tileHeight * RenderingSystem.PIXELS_TO_METRES) * levelFactory.collisionLayer.height as float
//		levelFactory.createScrollingYBoundaries(totalHeight)
		levelFactory.buildMap()
		levelFactory.createPlayer(camera)
	}

	@Override
	void render(float delta) {
		if(parent.state == GameJamGame.STATE.RUNNING) {
			Gdx.gl.glClearColor(0,0,0, 1)
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
			//Update the camera location before updating the systems.
			if(levelFactory.player) {
				SdBodyComponent playerBody = Mapper.bCom.get(levelFactory.player)
				if(playerBody.body.position.x > 20) {
					renderingSystem.getCamera().position.x = playerBody.body.position.x
				}
			}
			engine.update(delta)

			//Move to end game screen once all lives used up
			if(levelFactory.playerLives == -1) {
				parent.changeScreen(parent.ENDGAME)
			}

//			levelFactory.hud.setScore(levelFactory.playerScore)
//			levelFactory.hud.setLives(levelFactory.playerLives)
//			levelFactory.hud.setBullets(levelFactory.playerBullets)
//			levelFactory.hud.setGrenades(levelFactory.playerGrenades)
		}

		// Gif Recorder support
		if(parent.recorder) {
			parent.recorder.update()
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
