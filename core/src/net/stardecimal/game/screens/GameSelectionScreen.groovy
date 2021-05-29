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
		Table root = new Table(
				fillParent: true
		)
		Table innerTable = new Table(debug: false)
		ScrollPane scroll = new ScrollPane(innerTable)
		root.add(scroll)
		root.row()
		stage.addActor(root)

		TextButton pingPong = new TextButton("Ping Pong", skin)
		TextButton worm = new TextButton("Worm", skin)
		TextButton breakout = new TextButton("Breakout", skin)
		TextButton missileCommand = new TextButton("Missile Command", skin)
		TextButton spaceInvaders = new TextButton("Space Invaders", skin)
		TextButton asteroids = new TextButton("Asteroids", skin)
		TextButton tetris = new TextButton("Tetris", skin)
		TextButton pacman = new TextButton("Pacman", skin)
		TextButton back = new TextButton("Back", skin)

		[pingPong, worm, breakout, missileCommand, spaceInvaders, asteroids, tetris, pacman, back].each {
//			it.setTransform(true)
//			it.setScale(0.7)
			it.label.setFontScale(0.5)
		}

		innerTable.defaults().left().width(Gdx.graphics.width / 2 - 10).space(10)

		innerTable.add(pingPong)
		innerTable.add(worm)
		innerTable.row()
		innerTable.add(breakout)
		innerTable.add(missileCommand)
		innerTable.row()
		innerTable.add(spaceInvaders)
		innerTable.add(asteroids)
		innerTable.row()
		innerTable.add(tetris)
		innerTable.add(pacman)
		innerTable.row()
		innerTable.add(back)

		stage.setScrollFocus(scroll)
		scroll.validate()
		innerTable.validate()

//		innerTable.setTransform(true)
//		innerTable.setOrigin(100, 0)
//		innerTable.setScaleX(0.7)
		root.layout()


		back.addListener(new ChangeListener() {
			@Override
			void changed(ChangeListener.ChangeEvent event, Actor actor) {
				parent.changeScreen(parent.lastMenu)
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

		spaceInvaders.addListener(new ChangeListener() {
			@Override
			void changed(ChangeListener.ChangeEvent event, Actor actor) {
				parent.changeScreen(MyGames.SPACE_INVADERS)
			}
		})

		asteroids.addListener(new ChangeListener() {
			@Override
			void changed(ChangeListener.ChangeEvent event, Actor actor) {
				parent.changeScreen(MyGames.ASTEROIDS)
			}
		})

		tetris.addListener(new ChangeListener() {
			@Override
			void changed(ChangeListener.ChangeEvent event, Actor actor) {
				parent.changeScreen(MyGames.TETRIS)
			}
		})

		pacman.addListener(new ChangeListener() {
			@Override
			void changed(ChangeListener.ChangeEvent event, Actor actor) {
				parent.changeScreen(MyGames.PACMAN)
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