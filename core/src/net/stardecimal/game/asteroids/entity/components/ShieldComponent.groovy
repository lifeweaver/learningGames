package net.stardecimal.game.asteroids.entity.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

class ShieldComponent implements Component, Pool.Poolable {

	static int maxHits = 2
	int currentHits = maxHits

	@Override
	void reset() {
		currentHits = maxHits
	}
}
