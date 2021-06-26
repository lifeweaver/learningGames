package com.stardecimal.game.util

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
	Label score


	DefaultHud(SpriteBatch spriteBatch) {
		stage = new Stage(new ScreenViewport(), spriteBatch)
		table = new Table(fillParent: true)

		font = new BitmapFont(Gdx.files.internal("font/calibri14.fnt"), false)
		Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.RED)

		score = new Label("SCORE:" + String.format("%06d", 0), labelStyle)
		table.add(score).expand().center().top().padTop(10)
		table.row()

		stage.addActor(table)
	}

	void setScore(int value) {
		score.setText("SCORE:" + String.format("%06d", value))
	}

	@Override
	void dispose() {
		stage.dispose()
	}
}
