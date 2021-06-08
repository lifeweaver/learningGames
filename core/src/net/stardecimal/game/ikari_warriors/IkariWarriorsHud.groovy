package net.stardecimal.game.ikari_warriors

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

class IkariWarriorsHud implements HudOverlay, Disposable {
	Label score, lives, bullets, grenades

	IkariWarriorsHud(SpriteBatch spriteBatch) {
		stage = new Stage(new ScreenViewport(), spriteBatch)
		table = new Table(fillParent: true)

		font = new BitmapFont(Gdx.files.internal("font/calibri14.fnt"), false)
		Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.WHITE)

		score = new Label("SCORE: " + String.format("%05d", 0), labelStyle)
		lives = new Label("LIVES: " + String.format("%02d", 0), labelStyle)
		bullets = new Label("BULLETS: " + String.format("%02d", 0), labelStyle)
		grenades = new Label("GRENADES: " + String.format("%02d", 0), labelStyle)
		table.add(score).expand().center().top().padTop(5)
		table.row()
		table.defaults().left().fillX().padLeft(5)
		table.add(bullets)
		table.row()
		table.add(grenades)
		table.row()
		table.add(lives).padBottom(5)

		stage.addActor(table)
	}

	void setScore(int value) {
		score.setText("SCORE: " + String.format("%05d", value))
	}

	void setBullets(int value) {
		bullets.setText("BULLETS: " + String.format("%02d", value))
	}

	void setGrenades(int value) {
		grenades.setText("GRENADES: " + String.format("%02d", value))
	}

	void setLives(int value) {
		lives.setText("LIVES: " + String.format("%02d", value))
	}

	@Override
	void dispose() {
		stage.dispose()
	}
}