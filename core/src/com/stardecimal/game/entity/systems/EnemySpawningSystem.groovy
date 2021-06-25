package com.stardecimal.game.entity.systems

import com.badlogic.ashley.systems.IntervalSystem
import com.badlogic.gdx.math.Vector2
import com.stardecimal.game.LevelFactory
import com.stardecimal.game.entity.components.BulletComponent
import com.stardecimal.game.entity.util.Mapper
import com.stardecimal.game.util.DFUtils

class EnemySpawningSystem extends IntervalSystem {\
	LevelFactory levelFactory

	@SuppressWarnings("unchecked")
	EnemySpawningSystem(LevelFactory lvlf, float interval) {
		//Interval in seconds
		super(interval)
		levelFactory = lvlf
		processing = true
		priority = 20
	}

	@Override
	void updateInterval() {
		int direction = levelFactory.rand.nextInt(2) > 0 ? 1 : -1
		Vector2 target = Mapper.bCom.get(levelFactory.player).body.position
		Vector2 shooter = new Vector2(target.x + direction as float, target.y + 1 as float)
		float angle = DFUtils.vectorToAngle(DFUtils.aimTo(shooter, target))

		levelFactory.createShot(shooter, angle, BulletComponent.Owner.ENEMY)
	}

}
