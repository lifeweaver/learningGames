package net.stardecimal.game.entity.components

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
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
			DESTRUCTIBLE_SCENERY: 16,
			ENEMY_SPACESHIP: 17,
			ASTEROID: 18,
			MINI_ASTEROID: 19,
			MEDIUM_ASTEROID: 20,
			BLINKY: 21,
			PINKY: 22,
			INKY: 23,
			CLYDE: 24,
			GRENADE: 25,
			TANK: 26,
			GUN_SOLDIER: 27,
			DEFAULT: 999
	]

	static String getTypeName(int type) {
		return TYPES.find {it.value == type}?.key ?: null
	}

	static String getTypeName(Entity entity) {
		TypeComponent entityType = Mapper.typeCom.get(entity)
		if(entityType) {
			return TYPES.find {it.value == entityType.type}?.key ?: null
		}

		return "No TypeComponent found"
	}

	int type = TYPES.OTHER

	@Override
	void reset() {
		type = TYPES.OTHER
	}
}