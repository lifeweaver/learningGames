package net.stardecimal.game.desktop

import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration
import net.stardecimal.game.MyGames

class DesktopLauncher {
	static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration()
		config.title = "MyGame"
		new LwjglApplication(new MyGames(), config)
	}
}
