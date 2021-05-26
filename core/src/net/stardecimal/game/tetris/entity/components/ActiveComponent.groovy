package net.stardecimal.game.tetris.entity.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

class ActiveComponent implements Component, Pool.Poolable {
	boolean active = true

	@Override
	void reset() {
		active = true
	}
}
