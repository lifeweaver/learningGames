package net.stardecimal.game.worm.entity.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.utils.Pool

class PlayerComponent implements Component, Pool.Poolable {
	OrthographicCamera cam = null
	int length = 10
	List<float[]> rotations = []

	@Override
	void reset() {
		cam = null
		rotations = []
		length = 0
	}
}