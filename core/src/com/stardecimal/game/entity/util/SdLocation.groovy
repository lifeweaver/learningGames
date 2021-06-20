package com.stardecimal.game.entity.util

import com.badlogic.gdx.ai.utils.Location
import com.badlogic.gdx.math.Vector2
import com.stardecimal.game.util.DFUtils

class SdLocation implements Location<Vector2> {

	Vector2 position
	float orientation

	SdLocation() {
		this.position = new Vector2()
		this.orientation = 0
	}

	@Override
	Vector2 getPosition () {
		return position
	}

	@Override
	float getOrientation () {
		return orientation
	}

	@Override
	void setOrientation (float orientation) {
		this.orientation = orientation
	}

	@Override
	Location<Vector2> newLocation () {
		return new SdLocation()
	}

	@Override
	float vectorToAngle (Vector2 vector) {
		return DFUtils.vectorToAngle(vector)
	}

	@Override
	Vector2 angleToVector (Vector2 outVector, float angle) {
		return DFUtils.angleToVector(outVector, angle)
	}

}