package net.stardecimal.game.entity.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Pool

class VelocityComponent implements Component, Pool.Poolable {
	Vector2 linearVelocity = new Vector2()
	float angularVelocity = 0


	@Override
	void reset() {
		linearVelocity = new Vector2()
		angularVelocity = 0
	}
}
