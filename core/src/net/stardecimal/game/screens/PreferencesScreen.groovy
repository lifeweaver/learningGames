package net.stardecimal.game.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Event
import com.badlogic.gdx.scenes.scene2d.EventListener
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Slider
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent
import com.badlogic.gdx.utils.viewport.ScreenViewport
import net.stardecimal.game.MyGames
import net.stardecimal.game.loader.SdAssetManager

class PreferencesScreen extends ScreenAdapter {
	MyGames parent
	Stage stage
	private Label titleLabel
	private Label volumeMusicLabel
	private Label volumeSoundLabel
	private Label musicOnOffLabel
	private Label soundOnOffLabel

	PreferencesScreen(MyGames game) {
		parent = game
		stage = new Stage(new ScreenViewport())
	}

	@Override
	void render(float delta) {
		Gdx.gl.glClearColor(0f, 0f, 0f, 1)
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
		stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f) as float)
		stage.draw()
	}

	@Override
	void show () {
		Gdx.input.setInputProcessor(stage)
		stage.clear()
		// Create a table that fills the screen. Everything else will go inside this table.
		Table table = new Table(
				fillParent: true
		)

		stage.addActor(table)

		Skin skin = new Skin(Gdx.files.internal(SdAssetManager.skin))

		final Slider volumeMusicSlider = new Slider( 0f, 1f, 0.1f,false, skin )
		volumeMusicSlider.setValue( parent.getPreferences().getMusicVolume() )
		volumeMusicSlider.addListener(new EventListener() {
			@Override
			boolean handle(Event event) {
				parent.getPreferences().setMusicVolume( volumeMusicSlider.getValue() )
				return false
			}
		})

		final Slider soundMusicSlider = new Slider( 0f, 1f, 0.1f,false, skin )
		soundMusicSlider.setValue( parent.getPreferences().getMusicVolume() )
		soundMusicSlider.addListener(new EventListener() {
			@Override
			boolean handle(Event event) {
				parent.getPreferences().setSoundVolume( soundMusicSlider.getValue() )
				return false
			}
		})

		//music
		final CheckBox musicCheckbox = new CheckBox(null, skin)
		musicCheckbox.setChecked( parent.getPreferences().isMusicEnabled() )
		musicCheckbox.addListener( new EventListener() {
			@Override
			boolean handle(Event event) {
				boolean enabled = musicCheckbox.isChecked()
				parent.getPreferences().setMusicEnabled( enabled )
				return false
			}
		})

		final CheckBox soundEffectsCheckbox = new CheckBox(null, skin)
		soundEffectsCheckbox.setChecked( parent.getPreferences().isMusicEnabled() )
		soundEffectsCheckbox.addListener( new EventListener() {
			@Override
			boolean handle(Event event) {
				boolean enabled = soundEffectsCheckbox.isChecked()
				parent.getPreferences().setSoundEffectsEnabled( enabled )
				return false
			}
		})

		// return to main screen button
		final TextButton backButton = new TextButton("Back", skin, "small") // the extra argument here "small" is used to set the button to the smaller version instead of the big default version
		backButton.addListener(new ChangeListener() {
			@Override
			void changed(ChangeEvent event, Actor actor) {
				parent.changeScreen(parent.lastMenu)
			}
		})

		titleLabel = new Label( "Preferences", skin )
		volumeMusicLabel = new Label( 'Music Volume', skin )
		volumeSoundLabel = new Label( 'Sound Volume', skin )
		musicOnOffLabel = new Label( 'Music', skin )
		soundOnOffLabel = new Label( 'Sound Effect', skin )

		table.add(titleLabel).colspan(2)
		table.row().pad(10,0,0,10)
		table.add(volumeMusicLabel)
		table.add(volumeMusicSlider)
		table.row().pad(10,0,0,10)
		table.add(musicOnOffLabel)
		table.add(musicCheckbox)
		table.row().pad(10,0,0,10)
		table.add(volumeSoundLabel)
		table.add(soundMusicSlider)
		table.row().pad(10,0,0,10)
		table.add(soundOnOffLabel)
		table.add(soundEffectsCheckbox)
		table.row().pad(10,0,0,10)
		table.add(backButton).colspan(2)
	}

	@Override
	void resize (int width, int height) {
		stage.getViewport().update(width, height, true)
	}

	@Override
	void dispose () {
		stage.dispose()
	}
}
