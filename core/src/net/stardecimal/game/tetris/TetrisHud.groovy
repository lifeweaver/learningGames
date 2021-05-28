package net.stardecimal.game.tetris

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.viewport.ScreenViewport
import net.stardecimal.game.HudOverlay

class TetrisHud implements HudOverlay, Disposable {
	Label score, level

	TetrisHud(SpriteBatch spriteBatch) {
		stage = new Stage(new ScreenViewport(), spriteBatch)
		table = new Table(fillParent: true)

		font = new BitmapFont(Gdx.files.internal("font/calibri14.fnt"), false)
		Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.WHITE)

		score = new Label("SCORE: " + String.format("%05d", 0), labelStyle)
		level = new Label("LEVEL: " + String.format("%03d", 0), labelStyle)
		table.add(score).expandX().left().padLeft(190).padTop(-275)
		table.row()
		table.add(level).expandX().left().padLeft(190).padTop(-250)

		stage.addActor(table)
	}

	void setScore(int value) {
		score.setText("SCORE: " + String.format("%05d", value))
	}

	void setLevel(int value) {
		level.setText("LEVEL: " + String.format("%03d", value))
	}

	@Override
	void dispose() {
		stage.dispose()
	}
}