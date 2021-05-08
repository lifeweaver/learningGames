package net.stardecimal.game

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.graphics.GL20
import net.stardecimal.game.breakout.BreakoutScreen
import net.stardecimal.game.loader.SdAssetManager
import net.stardecimal.game.missilecommand.MissileCommandScreen
import net.stardecimal.game.pong.PongScreen
import net.stardecimal.game.screens.EndScreen
import net.stardecimal.game.screens.GameSelectionScreen
import net.stardecimal.game.screens.MenuScreen
import net.stardecimal.game.screens.PreferencesScreen
import net.stardecimal.game.worm.WormScreen

class MyGames extends Game {
//	private LoadingScreen loadingScreen

	private PreferencesScreen preferencesScreen
	private MenuScreen menuScreen
	private PongScreen pongScreen
	private GameSelectionScreen gameSectionScreen
	private EndScreen endScreen
	private AppPreferences appPreferences
	private WormScreen wormScreen
	private BreakoutScreen breakoutScreen
	private MissileCommandScreen missileCommandScreen
	SdAssetManager assetManager = new SdAssetManager()

	final static int MENU = 0
	final static int PREFERENCES = 1
	final static int PONG = 2
	final static int ENDGAME = 3
	final static int GAME_SELECTION = 4
	final static int WORM = 5
	final static int BREAKOUT = 6
	final static int MISSILE_COMMAND = 7
	Music playingSong

	//TODO
	//Add end game
	//Pause Menu
	//stop using .finishLoading() and have a progress screen with a loading bar.
	//Additionally you can automate your resource loading via:
	//https://github.com/raeleus/Java-Poet-and-libGDX-Example/wiki
	//Switch from using camera directly to viewport? https://github.com/libgdx/libgdx/wiki/Viewports
	// something else

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
					wormScreen = new WormScreen(this)
				} else {
					wormScreen.resetWorld()
				}

				this.setScreen(wormScreen)
				break

			case BREAKOUT:
				if(!breakoutScreen) {
					breakoutScreen = new BreakoutScreen(this)
				} else {
					breakoutScreen.resetWorld()
				}

				this.setScreen(breakoutScreen)
				break

			case MISSILE_COMMAND:
				if(!missileCommandScreen) {
					missileCommandScreen = new MissileCommandScreen(this)
				} else {
					missileCommandScreen.resetWorld()
				}

				this.setScreen(missileCommandScreen)
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
