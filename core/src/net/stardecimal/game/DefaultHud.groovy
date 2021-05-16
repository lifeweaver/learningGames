package net.stardecimal.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.viewport.ScreenViewport

class DefaultHud implements HudOverlay, Disposable {
	Label score, lives


	DefaultHud(SpriteBatch spriteBatch) {
		stage = new Stage(new ScreenViewport(), spriteBatch)
		table = new Table(fillParent: true)

		font = new BitmapFont(Gdx.files.internal("font/calibri14.fnt"), false)
		Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.WHITE)

		score = new Label("SCORE:" + String.format("%06d", 0), labelStyle)
		lives = new Label(String.format("%06d", 0), labelStyle)
		table.add(score).expand().center().top().padTop(10)
		table.row()
		table.add(lives).bottom().left().fillX().padBottom(10).padLeft(10)

		stage.addActor(table)
	}

	void setScore(int value) {
		score.setText("SCORE:" + String.format("%06d", value))
	}

	void setLives(int value) {
		lives.setText(String.format("%06d", value))
	}

	@Override
	void dispose() {
		stage.dispose()
	}
}
