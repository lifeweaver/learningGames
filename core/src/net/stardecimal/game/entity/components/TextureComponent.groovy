package net.stardecimal.game.entity.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.utils.Pool

class TextureComponent implements Component, Pool.Poolable {
	TextureRegion region = null
	Texture texture = null
	float offsetX = 0
	float offsetY = 0

	@Override
	void reset() {
		region = null
		texture = null
		offsetX = 0
		offsetY = 0
	}

}