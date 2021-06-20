package com.stardecimal.game.entity.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.utils.Pool

class SdBodyComponent implements Component, Pool.Poolable{
	Body body
	boolean isDead = false
	float width = 0
	float height = 0
	float invulnerabilityTime = 0

	@Override
	void reset() {
		body = null
		isDead = false
		width = 0
		height = 0
		invulnerabilityTime = 0
	}
}
