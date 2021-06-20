package net.stardecimal.game.util

import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Table

trait HudOverlay {
	Stage stage
	Table table
	BitmapFont font
}