package net.stardecimal.game.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.ScreenViewport
import groovy.json.JsonSlurper
import net.stardecimal.game.DefaultLevelFactory
import net.stardecimal.game.MyGames
import net.stardecimal.game.loader.SdAssetManager
import net.stardecimal.game.HighScores

class HighScoresScreen extends ScreenAdapter {
	private MyGames parent
	private Stage stage
	private HighScores highScores
	private DefaultLevelFactory levelFactory

	HighScoresScreen(MyGames game, DefaultLevelFactory lvlFactory) {
		parent = game
		stage = new Stage(new ScreenViewport())
		highScores = new HighScores()
		levelFactory = lvlFactory
	}

	@Override
	void show() {
		Gdx.input.setInputProcessor(stage)
		stage.clear()
		// Create a table that fills the screen. Everything else will go inside this table.
		Table table = new Table(
				fillParent: true
		)

		stage.addActor(table)
		Skin skin = new Skin(Gdx.files.internal(SdAssetManager.skin))

		// return to main screen button
		final TextButton backButton = new TextButton("Back", skin, "small") // the extra argument here "small" is used to set the button to the smaller version instead of the big default version
		backButton.addListener(new ChangeListener() {
			@Override
			void changed(ChangeListener.ChangeEvent event, Actor actor) {
				parent.changeScreen(MyGames.MENU)
			}
		})

		Label labelHighScores = new Label("High Scores", skin)
		table.add(labelHighScores).colspan(2)
		table.row().padTop(20)

		//Loop over high scores
		def jsonSlurper = new JsonSlurper()
		def parsedJson = jsonSlurper.parseText(highScores.getHighScores(levelFactory.gameName))
		parsedJson.each { Map highScore ->
			Label nameLabel = new Label("${highScore['name']}:", skin)
			Label scoreLabel = new Label("${highScore['score'].toString().padLeft(10, '0')}", skin)
			table.add(nameLabel).align(Align.left).uniformX()
			table.add(scoreLabel).align(Align.right).uniformX()
			table.row().padTop(10)
		}

		table.row().pad(10,0,0,10)
		table.add(backButton).colspan(2)
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
