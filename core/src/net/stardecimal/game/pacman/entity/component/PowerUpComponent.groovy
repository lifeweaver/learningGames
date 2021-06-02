package net.stardecimal.game.pacman.entity.component

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

class PowerUpComponent implements Component, Pool.Poolable {
	float activeTime = 0f

	@Override
	void reset() {
		activeTime = 0f
	}
}
