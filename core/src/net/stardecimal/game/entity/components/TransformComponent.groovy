package net.stardecimal.game.entity.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.Pool

class TransformComponent implements Component, Pool.Poolable {
	Vector3 position = new Vector3()
	Vector2 scale = new Vector2(1.0f, 1.0f)
	float rotation = 0.0f
	boolean isHidden = false

	@Override
	void reset() {
		rotation = 0.0f
		isHidden = false
		position = new Vector3()
		scale = new Vector2(1.0f, 1.0f)
	}
}
