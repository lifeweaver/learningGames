package net.stardecimal.game.entity.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.utils.Pool

//https://www.gamedevelopment.blog/full-libgdx-game-tutorial-particle-effects/
class ParticleEffectComponent implements Component, Pool.Poolable {
	ParticleEffectPool.PooledEffect particleEffect
	boolean isAttached = false
	float xOffset = 0
	float yOffset = 0
	float timeTilDeath = 0.5f
	boolean isDead = false
	boolean killOnParentBodyDeath = false
	Body attachedBody

	@Override
	void reset() {
		particleEffect.free() // free the pooled effect
		particleEffect = null // empty this component's particle effect
		xOffset = 0
		yOffset = 0
		isAttached = false
		isDead = false
		attachedBody = null
		timeTilDeath = 0.5f
		killOnParentBodyDeath = false
	}
}
