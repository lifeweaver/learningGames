package net.stardecimal.game.entity.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

class BulletComponent implements Component, Pool.Poolable {
	static enum Owner { ENEMY,PLAYER,SCENERY,NONE }

	public float xVel = 0
	public float yVel = 0
	public boolean isDead = false
	public Owner owner = Owner.NONE

	@Override
	void reset() {
		owner = Owner.NONE
		xVel = 0
		yVel = 0
		isDead = false
	}
}
