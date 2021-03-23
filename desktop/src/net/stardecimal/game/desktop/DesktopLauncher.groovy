package net.stardecimal.game.desktop

import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration
import net.stardecimal.game.PongGame

class DesktopLauncher {
	static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration()
		config.title = "Pong"
		new LwjglApplication(new PongGame(), config)
	}
}
