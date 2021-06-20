package com.stardecimal.game.entity.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.utils.Pool

class PlayerComponent implements Component, Pool.Poolable {
	OrthographicCamera cam = null

	@Override
	void reset() {
		cam = null
	}
}