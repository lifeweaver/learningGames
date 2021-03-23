package net.stardecimal.game.entity.components

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.utils.Pool

class CollisionComponent implements Component, Pool.Poolable {
	Entity collisionEntity

	@Override
	void reset() {
		collisionEntity = null
	}
}
