package net.stardecimal.game.missilecommand.entity.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IntervalSystem
import com.badlogic.gdx.math.Vector2
import net.stardecimal.game.entity.components.Mapper
import net.stardecimal.game.entity.components.SdBodyComponent
import net.stardecimal.game.entity.components.TypeComponent
import net.stardecimal.game.entity.systems.RenderingSystem
import net.stardecimal.game.missilecommand.LevelFactory
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class EnemySpawningSystem extends IntervalSystem {
	private LevelFactory levelFactory
	float missilesToSpawn = 1
	private static final Logger log = LoggerFactory.getLogger(LevelFactory)
	float missilesSpawned = 0

	@SuppressWarnings("unchecked")
	EnemySpawningSystem(LevelFactory lvlf, float interval) {
		//Interval in seconds
		super(interval)
		levelFactory = lvlf
		processing = false
		priority = 20
	}

	@Override
	void updateInterval() {
		missilesToSpawn.times {
			missilesSpawned += 1
			levelFactory.createEnemyMissile()
		}

		//Spawn a bomber plane every 7 missiles
		if(missilesSpawned % 7 == 0) {
			processEnemy(TypeComponent.TYPES.BOMBER_PLANE)
		}

		//Spawn a satellite every 10 missiles
		if(missilesSpawned % 10 == 0) {
			processEnemy(TypeComponent.TYPES.SATELLITE)
		}

		//Spawn a smart bomb every 15 missiles
		if(missilesSpawned % 15 == 0) {
//			processEnemy(TypeComponent.TYPES.SMART_BOMB)
		}
	}

	void processEnemy(int type) {
		Vector2 screenSize = RenderingSystem.getScreenSizeInMeters()
		float maxX = screenSize.x / RenderingSystem.PPM as float
		float maxY = screenSize.y / RenderingSystem.PPM as float
		List<Entity> enemies = engine.getEntities().findAll {
			Mapper.typeCom.get(it)?.type == type
		}

		//Kill any enemy outside the screen
		enemies.each {
			SdBodyComponent sdBody = Mapper.bCom.get(it)
			Vector2 pos = sdBody.body.position
			if(pos.x < 0 || pos.y < 0 || pos.x > maxX || pos.y > maxY) {
				sdBody.isDead = true
			}
		}

		//Only leave alive enemies in list
		enemies.removeAll {
			Mapper.bCom.get(it).isDead
		}

		//Don't spawn a enemy if one already exists
		if(enemies.isEmpty()) {
			if(type == TypeComponent.TYPES.BOMBER_PLANE) {
				levelFactory.createBomberPlane()
			} else if(type == TypeComponent.TYPES.SATELLITE) {
				levelFactory.createSatellite()
			} else if(type == TypeComponent.TYPES.SMART_BOMB) {
				levelFactory.createSmartBomb()
			}
		}
	}
}
