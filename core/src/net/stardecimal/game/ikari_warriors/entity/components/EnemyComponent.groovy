package net.stardecimal.game.ikari_warriors.entity.components

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.utils.Pool

class EnemyComponent implements Component, Pool.Poolable {

	float lastTimeFired = 0
	float firingDelay = 0
	int timesFired = 0
	final float FIRING_INTERVAL = 4
	final float FIRING_INTERVAL_BURST = 0.3
	Entity target = null

	@Override
	void reset() {
		lastTimeFired = 0
		firingDelay = 0
		target = null
		timesFired = 0
	}
}