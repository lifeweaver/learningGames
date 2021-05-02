package net.stardecimal.game.entity.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

class TypeComponent implements Component, Pool.Poolable {
	static final TYPES = [
			PLAYER: 0,
			ENEMY: 1,
			SCENERY: 2,
			OTHER: 3,
			SPRING: 4,
			BULLET: 5,
			SCORE_WALL: 6,
			ENEMY_EXPLODE: 7,
			ENEMY_DOUBLE: 8,
			POWER_UP: 9,
			CITY: 10,
			DEFENDER_MISSILE: 11,
			EXPLOSION: 12,
			BOMBER_PLANE: 13,
			SATELLITE: 14,
			SMART_BOMB: 15,
			DEFAULT: 999
	]

	static String getTypeName(int type) {
		return TYPES.find {it.value == type}?.key ?: null
	}

	int type = TYPES.OTHER

	@Override
	void reset() {
		type = TYPES.OTHER
	}
}