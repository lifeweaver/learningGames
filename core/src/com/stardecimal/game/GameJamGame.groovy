package com.stardecimal.game

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.graphics.Cursor
import com.badlogic.gdx.graphics.GL20
import io.anuke.gif.GifRecorder
import com.stardecimal.game.util.SdAssetManager
import com.stardecimal.game.screens.EndScreen
import com.stardecimal.game.screens.HighScoresScreen
import com.stardecimal.game.screens.MainGameScreen
import com.stardecimal.game.screens.MenuScreen
import com.stardecimal.game.screens.PauseScreen
import com.stardecimal.game.screens.PreferencesScreen
import com.stardecimal.game.util.AppPreferences
import com.stardecimal.game.util.MyUiInputProcessor

class GameJamGame extends Game {
//	private LoadingScreen loadingScreen
	static enum STATE { READY, RUNNING, PAUSED, LEVEL_END, OVER }

	private PreferencesScreen preferencesScreen
	private MenuScreen menuScreen
	private EndScreen endScreen
	private AppPreferences appPreferences
	private PauseScreen pauseScreen
	private HighScoresScreen highScoresScreen
	private MainGameScreen gameScreen
	SdAssetManager assetManager = new SdAssetManager()
	InputMultiplexer multiplexer = new InputMultiplexer()

	final static int MENU = 0
	final static int PREFERENCES = 1
	final static int ENDGAME = 2
	final static int HIGH_SCORES = 3
	final static int PAUSE = 4
	final static int GAME = 5

	int lastMenu = MENU
	int currentGame
	int currentScreen
	STATE state = STATE.READY
	MyUiInputProcessor uiInputProcessor
	GifRecorder recorder

	GameJamGame() {
		uiInputProcessor = new MyUiInputProcessor(this)
		multiplexer.addProcessor(uiInputProcessor)
	}

	void gameChange(int screen) {
		if(currentGame != screen) {
			//Remove all input processors and re-add the ui input processor
			multiplexer.clear()
			multiplexer.addProcessor(uiInputProcessor)

			//Null out every game
			gameScreen?.dispose()
			gameScreen = null
		}

		currentGame = screen
	}

	void changeScreen(int screen) {
		currentScreen = screen
		Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow)
		switch(screen) {
			case GAME:
				gameChange(screen)
				if(!gameScreen) {
					gameScreen = new MainGameScreen(this)
				} else if(lastMenu != PAUSE) {
					gameScreen.resetWorld()
				}

				this.setScreen(gameScreen)
				state = STATE.RUNNING
				break

			case MENU:
				state = STATE.READY
				lastMenu = screen
				menuScreen = menuScreen ?: new MenuScreen(this)
				this.setScreen(menuScreen)
				break

			case PREFERENCES:
				state = STATE.READY
				preferencesScreen = preferencesScreen ?: new PreferencesScreen(this)
				this.setScreen(preferencesScreen)
				break

			case ENDGAME:
				state = STATE.OVER
				lastMenu = screen
				endScreen = new EndScreen(this, this.screen.levelFactory)
				this.setScreen(endScreen)
				break

			case PAUSE:
				state = STATE.READY
				lastMenu = screen
				pauseScreen = pauseScreen ?: new PauseScreen(this)
				this.setScreen(pauseScreen)
				break

			case HIGH_SCORES:
				state = STATE.READY
				lastMenu = screen
				highScoresScreen = new HighScoresScreen(this, this.screen.levelFactory)
				this.setScreen(highScoresScreen)
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
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		super.render()
	}

	@Override
	void dispose () {
		assetManager.manager.dispose()
	}

	@Override
	void resize(int width, int height) {
		super.resize(width, height)

	}
}
