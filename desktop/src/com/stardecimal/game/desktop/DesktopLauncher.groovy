package com.stardecimal.game.desktop

import ch.qos.logback.classic.util.ContextInitializer
import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration
import com.stardecimal.game.GameJamGame

class DesktopLauncher {
	static void main (String[] arg) {
		System.setProperty(ContextInitializer.CONFIG_FILE_PROPERTY, "../config/logback.groovy")
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration()
		config.title = "MyGame"
		new LwjglApplication(new GameJamGame(), config)
	}
}
