package net.stardecimal.game

import com.badlogic.gdx.Game
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.graphics.g2d.BitmapFont
import net.stardecimal.game.loader.SdAssetManager
import net.stardecimal.game.screens.EndScreen
import net.stardecimal.game.screens.MenuScreen
import net.stardecimal.game.screens.MainScreen
import net.stardecimal.game.screens.PreferencesScreen

class MyGames extends Game {
//	private LoadingScreen loadingScreen

	private PreferencesScreen preferencesScreen
	private MenuScreen menuScreen
	private MainScreen mainScreen
	private EndScreen endScreen
	private AppPreferences appPreferences
	SdAssetManager assetManager = new SdAssetManager()

	final static int MENU = 0
	final static int PREFERENCES = 1
	final static int APPLICATION = 2
	final static int ENDGAME = 3
	int playerScore = 0
	int enemyScore = 0
	Music playingSong

	void changeScreen(int screen) {
		switch(screen) {
			case MENU:
				menuScreen = menuScreen ?: new MenuScreen(this)
				this.setScreen(menuScreen)
				break
			case PREFERENCES:
				preferencesScreen = preferencesScreen ?: new PreferencesScreen(this)
				this.setScreen(preferencesScreen)
				break
			case APPLICATION:
				if(!mainScreen) {
					mainScreen = new MainScreen(this)
				} else {
					mainScreen.resetWorld()
				}

				this.setScreen(mainScreen)
				break
			case ENDGAME:
				endScreen = endScreen ?: new EndScreen(this)
				this.setScreen(endScreen)
				break
		}
	}

	AppPreferences getPreferences() {
		appPreferences = appPreferences ?: new AppPreferences()
		return appPreferences
	}

	@Override
	void create () {
//		loadingScreen = new LoadingScreen(this)
		menuScreen = new MenuScreen(this)
		appPreferences = new AppPreferences()
//		setScreen(loadingScreen)
		setScreen(menuScreen)

		assetManager.queueAddMusic()
		assetManager.manager.finishLoading()
	}

	@Override
	void render () {
		super.render()
	}

	@Override
	void dispose () {
		assetManager.manager.dispose()
	}
}
