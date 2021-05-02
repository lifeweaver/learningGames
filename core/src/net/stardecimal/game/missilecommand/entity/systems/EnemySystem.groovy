package net.stardecimal.game.missilecommand.entity.systems

import com.badlogic.ashley.systems.IntervalSystem
import net.stardecimal.game.missilecommand.LevelFactory
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class EnemySystem extends IntervalSystem {
	private LevelFactory levelFactory
	float missilesToSpawn = 1
	private static final Logger log = LoggerFactory.getLogger(LevelFactory)
	float missilesSpawned = 0

	@SuppressWarnings("unchecked")
	EnemySystem(LevelFactory lvlf, float interval) {
		//Interval in seconds
		super(interval)
		levelFactory = lvlf
		processing = false
	}

	@Override
	void updateInterval() {
		missilesToSpawn.times {
			missilesSpawned += 1
			levelFactory.createEnemyMissile()
		}

		//Spawn a bomber plane every 10 missiles
//		if(missilesSpawned % 10 == 0) {
			levelFactory.createBomberPlane()
//		}

		//Spawn a satellite plane every 15 missiles
		if(missilesSpawned % 15 == 0) {

		}
	}
}
