package net.stardecimal.game.entity.components

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Pool

class BulletComponent implements Component, Pool.Poolable {
	static enum Owner { ENEMY,PLAYER,SCENERY,NONE }

	float xVel = 0
	float yVel = 0
	float distMoved = 0
	float maxDist = 0
	float maxLife = 0
	Vector2 startPos = null
	boolean isDead = false
	Owner owner = Owner.NONE
	Entity particleEffect = null

	@Override
	void reset() {
		owner = Owner.NONE
		xVel = 0
		yVel = 0
		distMoved = 0
		maxDist = 0
		maxLife = 0
		startPos = null
		isDead = false
		particleEffect = null
	}
}
