package net.stardecimal.game.ikari_warriors.entity.systems

import com.badlogic.ashley.systems.IntervalSystem
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import net.stardecimal.game.entity.components.Mapper
import net.stardecimal.game.entity.components.TypeComponent
import net.stardecimal.game.entity.systems.RenderingSystem
import net.stardecimal.game.ikari_warriors.LevelFactory
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class EnemySpawningSystem extends IntervalSystem {
	private LevelFactory levelFactory
	private static final Logger log = LoggerFactory.getLogger(LevelFactory)

	@SuppressWarnings("unchecked")
	EnemySpawningSystem(LevelFactory lvlf, float interval) {
		//Interval in seconds
		super(interval)
		levelFactory = lvlf
		priority = 20
	}

	@Override
	void updateInterval() {
		levelFactory.createSoldier(determineSpawnPoint(), TypeComponent.TYPES.GUN_SOLDIER)

	}

	Vector2 determineSpawnPoint() {
		if(levelFactory.player && Mapper.bCom.get(levelFactory.player)) {
			Vector2 playerPos = Mapper.bCom.get(levelFactory.player).body.position
			return new Vector2(playerPos.x, playerPos.y + 30 as float)
		}
		Vector3 center = engine.getSystem(RenderingSystem).camera.position
		return new Vector2(center.x, center.y + 30 as float)
	}
}
