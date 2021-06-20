package com.stardecimal.game.entity.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

class StateComponent implements Component, Pool.Poolable {
	static final int STATE_NORMAL  = 0
	static final int STATE_JUMPING = 1
	static final int STATE_FALLING = 2
	static final int STATE_MOVING  = 3
	static final int STATE_HIT     = 4
	static final int STATE_FLEEING = 5
	static final int STATE_PATROL = 6
	static final int STATE_SEEKING = 7

	private int state = 0
	float time = 0.0f
	boolean isLooping = false

	void setState(int newState) {
		state = newState
		time = 0.0f
	}

	int getState() {
		return state
	}

	@Override
	void reset() {
		state = 0
		time = 0.0f
		isLooping = false
	}
}
