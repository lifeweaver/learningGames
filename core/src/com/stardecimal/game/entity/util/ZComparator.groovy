package com.stardecimal.game.entity.util

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.stardecimal.game.entity.components.TransformComponent

class ZComparator implements Comparator<Entity> {
	private ComponentMapper<TransformComponent> cmTrans

	ZComparator() {
		cmTrans = ComponentMapper.getFor(TransformComponent.class)
	}

	@Override
	int compare(Entity entityA, Entity entityB) {
		if(entityA == null || entityB == null) {
			return 0
		}
		float az = cmTrans.get(entityA).position.z
		float bz = cmTrans.get(entityB).position.z
		int res = 0
		if(az > bz) {
			res = 1
		} else if (az < bz) {
			res = -1
		}
		return res
	}

}
