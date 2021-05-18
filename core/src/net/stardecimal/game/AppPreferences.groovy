package net.stardecimal.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Preferences

class AppPreferences {
	private static final String PREF_MUSIC_VOLUME = "volume"
	private static final String PREF_MUSIC_ENABLED = "music.enabled"
	private static final String PREF_SOUND_ENABLED = "sound.enabled"
	private static final String PREF_SOUND_VOL = "sound"
	private static final String PREFS_NAME = "learningGames"

	protected Preferences getPrefs() {
		return Gdx.app.getPreferences(PREFS_NAME)
	}

	boolean isSoundEffectsEnabled() {
		return getPrefs().getBoolean(PREF_SOUND_ENABLED, true)
	}

	void setSoundEffectsEnabled(boolean soundEffectsEnabled) {
		getPrefs().putBoolean(PREF_SOUND_ENABLED, soundEffectsEnabled)
		getPrefs().flush()
	}

	boolean isMusicEnabled() {
		return getPrefs().getBoolean(PREF_MUSIC_ENABLED, true)
	}

	void setMusicEnabled(boolean musicEnabled) {
		getPrefs().putBoolean(PREF_MUSIC_ENABLED, musicEnabled)
		getPrefs().flush()
	}

	float getMusicVolume() {
		return getPrefs().getFloat(PREF_MUSIC_VOLUME, 0.5f)
	}

	void setMusicVolume(float volume) {
		getPrefs().putFloat(PREF_MUSIC_VOLUME, volume)
		getPrefs().flush()
	}

	float getSoundVolume() {
		return getPrefs().getFloat(PREF_SOUND_VOL, 0.5f)
	}

	void setSoundVolume(float volume) {
		getPrefs().putFloat(PREF_SOUND_VOL, volume)
		getPrefs().flush()
	}
}
