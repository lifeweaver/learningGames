package net.stardecimal.game.entity.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Pool

class VelocityComponent implements Component, Pool.Poolable {
	Vector2 linearVelocity = new Vector2(0, 0)
	float angularVelocity = 0
	boolean removeAfterProcessing = true
	boolean applyVelocityToJointedBodies = true


	@Override
	void reset() {
		linearVelocity = new Vector2(0, 0)
		angularVelocity = 0
		removeAfterProcessing = true
		applyVelocityToJointedBodies = true
	}
}
