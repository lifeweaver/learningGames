package net.stardecimal.game.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.GL20
import net.stardecimal.game.GameJamGame
import net.stardecimal.game.LevelFactory
import net.stardecimal.game.entity.components.Mapper
import net.stardecimal.game.entity.components.SdBodyComponent
import net.stardecimal.game.entity.systems.RenderingSystem
import net.stardecimal.game.util.GameScreen

class MainGameScreen extends ScreenAdapter implements GameScreen {
	LevelFactory levelFactory
	RenderingSystem renderingSystem

	MainGameScreen(final GameJamGame game) {
		init(game, LevelFactory.class)
		levelFactory = (LevelFactory) lvlFactory
		levelFactory.gameName = 'Balls - a rolling story'
	}

	void resetWorld() {
		reset()
		levelFactory.playerScore = 0
		levelFactory.enemyScore = 0
		levelFactory.playerLives = 3
//		float totalHeight = (levelFactory.collisionLayer.tileHeight * RenderingSystem.PIXELS_TO_METRES) * levelFactory.collisionLayer.height as float
//		levelFactory.createScrollingYBoundaries(totalHeight)
//		levelFactory.createPlayer(camera)
	}

	@Override
	void render(float delta) {
		if(parent.state == GameJamGame.STATE.RUNNING) {
			Gdx.gl.glClearColor(0,0,0, 1)
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
			//Update the camera location before updating the systems.
			if(levelFactory.player) {
				SdBodyComponent playerBody = Mapper.bCom.get(levelFactory.player)
				renderingSystem.getCamera().position.x = playerBody.body.position.x
				renderingSystem.getCamera().position.y = playerBody.body.position.y
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
