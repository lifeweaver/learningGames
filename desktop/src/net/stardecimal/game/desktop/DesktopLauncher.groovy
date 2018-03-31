package net.stardecimal.game.desktop

import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration
import net.stardecimal.game.PongGame

class DesktopLauncher {
	static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration()
		config.title = "Pong"
		config.useGL30 = false
		config.width = 800
		config.height = 480
		new LwjglApplication(new PongGame(), config)
	}
}
