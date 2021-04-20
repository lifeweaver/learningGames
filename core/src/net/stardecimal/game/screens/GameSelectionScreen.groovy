package net.stardecimal.game.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.utils.viewport.ScreenViewport
import net.stardecimal.game.MyGames
import net.stardecimal.game.loader.SdAssetManager

class GameSelectionScreen extends ScreenAdapter {
	MyGames parent
	Stage stage
	Skin skin

	GameSelectionScreen(MyGames game) {
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
	void show () {
		Gdx.input.setInputProcessor(stage)
		stage.clear()
		// Create a table that fills the screen. Everything else will go inside this table.
		Table table = new Table(
				fillParent: true,
				debug: false
		)
		Table innerTable = new Table()
		ScrollPane scroll = new ScrollPane(innerTable)
		table.add(scroll)
		table.row()

		stage.addActor(table)

		TextButton pingPong = new TextButton("Ping Pong", skin)
		TextButton worm = new TextButton("Worm", skin)
		TextButton breakout = new TextButton("Breakout", skin)
		TextButton missileCommand = new TextButton("Missile Command", skin)
		TextButton back = new TextButton("Back", skin)

		innerTable.add(pingPong).fillX().uniformX()
		innerTable.row().pad(5, 0, 5, 0)
		innerTable.add(worm).fillX().uniformX()
		innerTable.row()
		innerTable.add(breakout).fillX().uniformX()
		innerTable.row().pad(5, 0, 5, 0)
		innerTable.add(missileCommand).fillX().uniformX()
		innerTable.row().pad(5, 0, 5, 0)
		innerTable.add(back).fillX().uniformX()
		table.layout()

		back.addListener(new ChangeListener() {
			@Override
			void changed(ChangeListener.ChangeEvent event, Actor actor) {
				parent.changeScreen(MyGames.MENU)
			}
		})

		pingPong.addListener(new ChangeListener() {
			@Override
			void changed(ChangeListener.ChangeEvent event, Actor actor) {
				parent.changeScreen(MyGames.PONG)
			}
		})

		worm.addListener(new ChangeListener() {
			@Override
			void changed(ChangeListener.ChangeEvent event, Actor actor) {
				parent.changeScreen(MyGames.WORM)
			}
		})

		breakout.addListener(new ChangeListener() {
			@Override
			void changed(ChangeListener.ChangeEvent event, Actor actor) {
				parent.changeScreen(MyGames.BREAKOUT)
			}
		})

		missileCommand.addListener(new ChangeListener() {
			@Override
			void changed(ChangeListener.ChangeEvent event, Actor actor) {
				parent.changeScreen(MyGames.MISSILE_COMMAND)
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