package net.stardecimal.game

import com.badlogic.gdx.Game
import com.badlogic.gdx.audio.Music
import net.stardecimal.game.loader.SdAssetManager
import net.stardecimal.game.screens.EndScreen
import net.stardecimal.game.screens.GameSelectionScreen
import net.stardecimal.game.screens.MenuScreen
import net.stardecimal.game.screens.PongScreen
import net.stardecimal.game.screens.PreferencesScreen
import net.stardecimal.game.screens.WormScreen

class MyGames extends Game {
//	private LoadingScreen loadingScreen

	private PreferencesScreen preferencesScreen
	private MenuScreen menuScreen
	private PongScreen pongScreen
	private GameSelectionScreen gameSectionScreen
	private EndScreen endScreen
	private AppPreferences appPreferences
	private WormScreen wormScreen
	SdAssetManager assetManager = new SdAssetManager()

	final static int MENU = 0
	final static int PREFERENCES = 1
	final static int PONG = 2
	final static int ENDGAME = 3
	final static int GAME_SELECTION = 4
	final static int WORM = 5
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

			case PONG:
				if(!pongScreen) {
					pongScreen = new PongScreen(this)
				} else {
					pongScreen.resetWorld()
				}

				this.setScreen(pongScreen)
				break

			case WORM:
				if(!wormScreen) {
					wormScreen = new PongScreen(this)
				} else {
					wormScreen.resetWorld()
				}

				this.setScreen(wormScreen)
				break

			case GAME_SELECTION:
				gameSectionScreen = gameSectionScreen ?: new GameSelectionScreen(this)
				this.setScreen(gameSectionScreen)
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
