package net.stardecimal.game.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.utils.viewport.ScreenViewport
import net.stardecimal.game.MyGames
import net.stardecimal.game.loader.SdAssetManager

class PauseScreen extends ScreenAdapter {
	private MyGames parent
	private Skin skin
	private Stage stage

	PauseScreen(MyGames game) {
		parent = game
		stage = new Stage(new ScreenViewport())
		parent.assetManager.queueAddSkin()
		parent.assetManager.manager.finishLoading()
		skin = parent.assetManager.manager.get(SdAssetManager.skin)
	}

	@Override
	void render(float delta) {
		Gdx.gl.glClearColor(0f, 0f, 0f, 1)
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
		stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f) as float)
		stage.draw()
	}

	@Override
	void show() {
		Gdx.input.setInputProcessor(stage)
		stage.clear()
		// Create a table that fills the screen. Everything else will go inside this table.
		Table table = new Table(
				fillParent: true,
				debug: false
		)

		stage.addActor(table)

		TextButton resume = new TextButton("Resume", skin)
		TextButton preferences = new TextButton("Preferences", skin)
		TextButton mainMenu = new TextButton("Main Menu", skin)
		TextButton exit = new TextButton("Exit", skin)

		table.add(resume).fillX().uniformX()
		table.row().pad(10, 0, 10, 0)
		table.add(preferences).fillX().uniformX()
		table.row()
		table.add(mainMenu).fillX().uniformX()
		table.row().pad(10, 0, 10, 0)
		table.add(exit).fillX().uniformX()

		resume.addListener(new ChangeListener() {
			@Override
			void changed(ChangeListener.ChangeEvent event, Actor actor) {
				parent.changeScreen(parent.currentGame)
			}
		})

		preferences.addListener(new ChangeListener() {
			@Override
			void changed(ChangeListener.ChangeEvent event, Actor actor) {
				parent.changeScreen(MyGames.PREFERENCES)
			}
		})

		mainMenu.addListener(new ChangeListener() {
			@Override
			void changed(ChangeListener.ChangeEvent event, Actor actor) {
				parent.changeScreen(MyGames.MENU)
			}
		})

		exit.addListener(new ChangeListener() {
			@Override
			void changed(ChangeListener.ChangeEvent event, Actor actor) {
				Gdx.app.exit()
			}
		})
	}

	@Override
	void resize(int width, int height) {
		stage.getViewport().update(width, height, true)
	}

	@Override
	void dispose() {
		stage.dispose()
	}
}
