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
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent
import com.badlogic.gdx.utils.viewport.ScreenViewport
import net.stardecimal.game.MyGames
import net.stardecimal.game.loader.SdAssetManager

class MenuScreen extends ScreenAdapter {
	MyGames parent
	Stage stage
	Skin skin
//	TextureRegion background

	MenuScreen(MyGames game) {
		parent = game
		stage = new Stage(new ScreenViewport())
		parent.assetManager.queueAddSkin()
		parent.assetManager.manager.finishLoading()
		skin = parent.assetManager.manager.get(SdAssetManager.skin)
//		background = DFUtils.makeTextureRegion(5f, 5f, '#000000')
	}

	@Override
	void render(float delta) {
		Gdx.gl.glClearColor(0f, 0f, 0f, 1)
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
		stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f) as float)
		stage.draw()
	}

	@Override
	void show () {
		Gdx.input.setInputProcessor(stage)
		stage.clear()
		// Create a table that fills the screen. Everything else will go inside this table.
		Table table = new Table(
				fillParent: true,
				debug: false
		)
//		table.background = new TiledDrawable(background)

		stage.addActor(table)

		TextButton newGame = new TextButton("New Game", skin)
		TextButton preferences = new TextButton("Preferences", skin)
		TextButton exit = new TextButton("Exit", skin)

		table.add(newGame).fillX().uniformX()
		table.row().pad(10, 0, 10, 0)
		table.add(preferences).fillX().uniformX()
		table.row()
		table.add(exit).fillX().uniformX()

		exit.addListener(new ChangeListener() {
			@Override
			void changed(ChangeEvent event, Actor actor) {
				Gdx.app.exit()
			}
		})

		newGame.addListener(new ChangeListener() {
			@Override
			void changed(ChangeEvent event, Actor actor) {
				parent.changeScreen(MyGames.APPLICATION)
			}
		})

		preferences.addListener(new ChangeListener() {
			@Override
			void changed(ChangeEvent event, Actor actor) {
				parent.changeScreen(MyGames.PREFERENCES)
			}
		})
	}

	@Override
	void resize (int width, int height) {
		stage.getViewport().update(width, height, true)
	}

	@Override
	void dispose () {
		stage.dispose()
	}
}