package net.stardecimal.game.entity.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

class TypeComponent implements Component, Pool.Poolable {
	static final int PLAYER  = 0
	static final int ENEMY   = 1
	static final int SCENERY = 2
	static final int OTHER   = 3
	static final int SPRING  = 4
	static final int BULLET  = 5
	static final int SCORE_WALL  = 6
	static final int ENEMY_EXPLODE  = 7
	static final int ENEMY_DOUBLE  = 8
	static final int POWER_UP  = 9
	static final int CITY  = 10
	static final int DEFENDER_MISSILE  = 11
	static final int EXPLOSION  = 12
	static final int DEFAULT = 999

	static final TYPES = [
			PLAYER: PLAYER,
			ENEMY: ENEMY,
			SCENERY: SCENERY,
			OTHER: OTHER,
			SPRING: SPRING,
			BULLET: BULLET,
			SCORE_WALL: SCORE_WALL,
			ENEMY_EXPLODE: ENEMY_EXPLODE,
			ENEMY_DOUBLE: ENEMY_DOUBLE,
			POWER_UP: POWER_UP,
			CITY: CITY,
			DEFENDER_MISSILE: DEFENDER_MISSILE,
			EXPLOSION: EXPLOSION,
			DEFAULT: DEFAULT
	]

	static String getTypeName(int type) {
		return TYPES.find {it.value == type}?.key ?: null
	}

	int type = OTHER

	@Override
	void reset() {
		type = OTHER
	}
}