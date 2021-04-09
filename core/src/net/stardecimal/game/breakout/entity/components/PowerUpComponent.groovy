package net.stardecimal.game.breakout.entity.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

class PowerUpComponent  implements Component, Pool.Poolable{
	static enum Type { NONE, NO_BOUNCE }
	Type type = Type.NONE
	int noBounceCount = 0

	@Override
	void reset() {
		type = Type.NONE
		noBounceCount = 0
	}
}
