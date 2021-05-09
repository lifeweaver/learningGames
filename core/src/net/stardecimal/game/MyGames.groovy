package net.stardecimal.game

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.graphics.Cursor
import com.badlogic.gdx.graphics.GL20
import net.stardecimal.game.breakout.BreakoutScreen
import net.stardecimal.game.loader.SdAssetManager
import net.stardecimal.game.missilecommand.MissileCommandScreen
import net.stardecimal.game.pong.PongScreen
import net.stardecimal.game.screens.EndScreen
import net.stardecimal.game.screens.GameSelectionScreen
import net.stardecimal.game.screens.MenuScreen
import net.stardecimal.game.screens.PauseScreen
import net.stardecimal.game.screens.PreferencesScreen
import net.stardecimal.game.spaceinvaders.SpaceInvadersScreen
import net.stardecimal.game.worm.WormScreen

class MyGames extends Game {
//	private LoadingScreen loadingScreen
	static enum STATE { READY, RUNNING, PAUSED, LEVEL_END, OVER }

	private PreferencesScreen preferencesScreen
	private MenuScreen menuScreen
	private PongScreen pongScreen
	private GameSelectionScreen gameSectionScreen
	private EndScreen endScreen
	private AppPreferences appPreferences
	private WormScreen wormScreen
	private BreakoutScreen breakoutScreen
	private MissileCommandScreen missileCommandScreen
	private PauseScreen pauseScreen
	private SpaceInvadersScreen spaceInvadersScreen
	SdAssetManager assetManager = new SdAssetManager()
	InputMultiplexer multiplexer = new InputMultiplexer()

	final static int MENU = 0
	final static int PREFERENCES = 1
	final static int PONG = 2
	final static int ENDGAME = 3
	final static int GAME_SELECTION = 4
	final static int WORM = 5
	final static int BREAKOUT = 6
	final static int MISSILE_COMMAND = 7
	final static int PAUSE = 8
	final static int SPACE_INVADERS = 9
	Music playingSong
	int lastMenu = MENU
	int currentGame
	int currentScreen
	STATE state = STATE.READY
	MyUiInputProcessor uiInputProcessor

	//TODO
	//use https://github.com/libgdx/libgdx/wiki/Texture-packer
	//stop using .finishLoading() and have a progress screen with a loading bar.
	//Additionally you can automate your resource loading via:
	//https://github.com/raeleus/Java-Poet-and-libGDX-Example/wiki
	//Switch from using camera directly to viewport? https://github.com/libgdx/libgdx/wiki/Viewports
	// something else
	MyGames() {
		uiInputProcessor = new MyUiInputProcessor(this)
		multiplexer.addProcessor(uiInputProcessor)
	}

	void gameChange(int screen) {
		if(currentGame != screen) {
			//Remove all input processors and re-add the ui input processor
			multiplexer.clear()
			multiplexer.addProcessor(uiInputProcessor)

			//Null out every game
			pongScreen?.dispose()
			pongScreen = null
			wormScreen?.dispose()
			wormScreen = null
			breakoutScreen?.dispose()
			breakoutScreen = null
			missileCommandScreen?.dispose()
			missileCommandScreen = null
			spaceInvadersScreen?.dispose()
			spaceInvadersScreen = null
		}

		currentGame = screen
	}

	void changeScreen(int screen) {
		currentScreen = screen
		Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow)
		switch(screen) {
			case PONG:
				gameChange(screen)
				if(!pongScreen) {
					pongScreen = new PongScreen(this)
				} else if(lastMenu != PAUSE) {
					pongScreen.resetWorld()
				}

				this.setScreen(pongScreen)
				state = STATE.RUNNING
				break

			case WORM:
				gameChange(screen)
				if(!wormScreen) {
					wormScreen = new WormScreen(this)
				} else if(lastMenu != PAUSE) {
					wormScreen.resetWorld()
				}

				this.setScreen(wormScreen)
				state = STATE.RUNNING
				break

			case BREAKOUT:
				gameChange(screen)
				if(!breakoutScreen) {
					breakoutScreen = new BreakoutScreen(this)
				} else if(lastMenu != PAUSE) {
					breakoutScreen.resetWorld()
				}

				this.setScreen(breakoutScreen)
				state = STATE.RUNNING
				break

			case MISSILE_COMMAND:
				gameChange(screen)
				if(!missileCommandScreen) {
					missileCommandScreen = new MissileCommandScreen(this)
				} else if(lastMenu != PAUSE) {
					missileCommandScreen.resetWorld()
				}

				this.setScreen(missileCommandScreen)
				state = STATE.RUNNING
				break


			case MISSILE_COMMAND:
				gameChange(screen)
				if(!spaceInvadersScreen) {
					spaceInvadersScreen = new SpaceInvadersScreen(this)
				} else if(lastMenu != PAUSE) {
					spaceInvadersScreen.resetWorld()
				}

				this.setScreen(spaceInvadersScreen)
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

			case GAME_SELECTION:
				state = STATE.READY
				gameSectionScreen = gameSectionScreen ?: new GameSelectionScreen(this)
				this.setScreen(gameSectionScreen)
				break

			case ENDGAME:
				state = STATE.OVER
				lastMenu = screen
				endScreen = endScreen ?: new EndScreen(this, this.screen.levelFactory)
				this.setScreen(endScreen)
				break

			case PAUSE:
				state = STATE.READY
				lastMenu = screen
				pauseScreen = pauseScreen ?: new PauseScreen(this)
				this.setScreen(pauseScreen)
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
