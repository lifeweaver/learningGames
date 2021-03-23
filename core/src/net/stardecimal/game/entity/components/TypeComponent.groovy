package net.stardecimal.game.entity.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

class TypeComponent implements Component, Pool.Poolable {
	static final int PLAYER  = 0
	static final int ENEMY   = 1
	static final int SCENERY = 2
	static final int OTHER   = 3
	static final int SPRING  = 4
	static final int BULLET  = 5
	static final int SCORE_WALL  = 5

	int type = OTHER

	@Override
	void reset() {
		type = OTHER
	}
}