package net.stardecimal.game.entity.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

class EnemyComponent implements Component, Pool.Poolable {

	float xPosCenter = -1
	boolean isGoingLeft = false

	@Override
	void reset() {
		xPosCenter = -1
		isGoingLeft = false
	}
}