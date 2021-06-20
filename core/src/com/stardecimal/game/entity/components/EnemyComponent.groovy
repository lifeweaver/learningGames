package com.stardecimal.game.entity.components

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.utils.Pool

class EnemyComponent implements Component, Pool.Poolable {

	float xPosCenter = -1
	boolean isGoingLeft = false
	Entity target = null

	@Override
	void reset() {
		xPosCenter = -1
		isGoingLeft = false
		target = null
	}
}