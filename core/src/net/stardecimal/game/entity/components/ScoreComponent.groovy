package net.stardecimal.game.entity.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

class ScoreComponent implements Component, Pool.Poolable {
	int worth = 0

	@Override
	void reset() {
		worth = 0
	}

}
