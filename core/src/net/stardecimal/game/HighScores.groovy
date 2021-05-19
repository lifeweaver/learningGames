package net.stardecimal.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Preferences

class HighScores {
	private static final String PREF_HIGH_SCORES = "high_scores"
	private static final String DEFAULT_HIGH_SCORES = '[{"name": "Captain", "score": 1000},{"name": "Senior first officer", "score": 800},{"name": "First officer", "score": 600},{"name": "Second officer", "score": 400},{"name": "Cadet", "score": 100}]'

	protected Preferences getPrefs(String game) {
		return Gdx.app.getPreferences(game)
	}

	String getHighScores(String game) {
		return getPrefs(game).getString(PREF_HIGH_SCORES, DEFAULT_HIGH_SCORES)
	}

	void setHighScores(String game, String highScores) {
		getPrefs(game).putString(PREF_HIGH_SCORES, highScores)
		getPrefs(game).flush()
	}
}
