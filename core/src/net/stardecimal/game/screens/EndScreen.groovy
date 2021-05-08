package net.stardecimal.game.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent
import com.badlogic.gdx.scenes.scene2d.utils.TiledDrawable
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.ScreenViewport
import net.stardecimal.game.DefaultLevelFactory
import net.stardecimal.game.MyGames

class EndScreen extends ScreenAdapter {
	private MyGames parent
	private Skin skin
	private Stage stage
	private TextureAtlas atlas
	private TextureAtlas.AtlasRegion background
	private DefaultLevelFactory levelFactory

	EndScreen(MyGames game, DefaultLevelFactory lvlFactory) {
		parent = game
		levelFactory = lvlFactory
	}

	@Override
	void show() {
		// get skin
		skin = parent.assetManager.manager.get("skin/glassy-ui.json")
//		atlas = parent.assetManager.manager.get("images/loading.atlas")
//		background = atlas.findRegion("flamebackground")

		// create button to go back to manu
		TextButton menuButton = new TextButton("Back", skin, "small")

		// create button listener
		menuButton.addListener(new ChangeListener() {
			@Override
			void changed(ChangeEvent event, Actor actor) {
				println("To the MENU")
				parent.changeScreen(MyGames.MENU)
			}
		})

		// create stage and set it as input processor
		stage = new Stage(new ScreenViewport())
		Gdx.input.setInputProcessor(stage)

		// create table to layout iutems we will add
		Table table = new Table()
		table.setFillParent(true)
//		table.setBackground(new TiledDrawable(background))

		//create a Labels showing the score and some credits
		Label labelScore = new Label("Score " + levelFactory.playerScore, skin)
		Label labelCredits = new Label("Credits:", skin)
		Label labelCredits1 = new Label("Game Design by ", skin)
		Label labelCredits2 = new Label("Lifeweaver", skin)
		Label labelCredits3 = new Label("Art Design by ", skin)
		Label labelCredits4 = new Label("Lifeweaver", skin)

		// add items to table
		table.add(labelScore).colspan(2)
		table.row().padTop(10)
		table.add(labelCredits).colspan(2)
		table.row().padTop(10)
		table.add(labelCredits1).uniformX().align(Align.left)
		table.add(labelCredits2).uniformX().align(Align.left)
		table.row().padTop(10)
		table.add(labelCredits3).uniformX().align(Align.left)
		table.add(labelCredits4).uniformX().align(Align.left)
		table.row().padTop(50)
		table.add(menuButton).colspan(2)

		//add table to stage
		stage.addActor(table)

	}

	@Override
	void render(float delta) {
		// clear the screen ready for next set of images to be drawn
		Gdx.gl.glClearColor(0f, 0f, 0f, 1)
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

		stage.act()
		stage.draw()
	}

	@Override
	void resize(int width, int height) {
		// change the stage's viewport when teh screen size is changed
		stage.getViewport().update(width, height, true)
	}
}
