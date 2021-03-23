package net.stardecimal.game.entity.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.utils.Pool

class SdBodyComponent implements Component, Pool.Poolable{
	Body body
	boolean isDead = false

	@Override
	void reset() {
		body = null
		isDead = false
	}
}
