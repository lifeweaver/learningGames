package net.stardecimal.game.missilecommand.entity.components

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.utils.Pool

class EnemyComponent implements Component, Pool.Poolable {

	float missilesFired = 0
	float lastTimeFired = 0
	float fireDelay = 0
	final float firingDelay = 4
	Entity target = null
	boolean hasSplit = false
	float splitCheckInterval = 1
	float splitCheckCoolDown = splitCheckInterval

	@Override
	void reset() {
		lastTimeFired = 0
		missilesFired = 0
		fireDelay = 0
		target = null
		hasSplit = false
		splitCheckInterval = 1
		splitCheckCoolDown = splitCheckInterval
	}
}
