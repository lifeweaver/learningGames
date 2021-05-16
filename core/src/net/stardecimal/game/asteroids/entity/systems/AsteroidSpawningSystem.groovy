package net.stardecimal.game.asteroids.entity.systems

import com.badlogic.ashley.systems.IntervalSystem
import net.stardecimal.game.asteroids.LevelFactory
import net.stardecimal.game.entity.components.Mapper
import net.stardecimal.game.entity.components.TypeComponent

class AsteroidSpawningSystem extends IntervalSystem {
	LevelFactory levelFactory

	@SuppressWarnings("unchecked")
	AsteroidSpawningSystem(LevelFactory lvlFactory, float interval) {
		super(interval)
		levelFactory = lvlFactory
	}

	@Override
	void updateInterval() {
		int asteroids = engine.getEntities().each {
			Mapper.typeCom.get(it).type == TypeComponent.TYPES.ASTEROID
		}.size()

		if(asteroids < 5) {
			levelFactory.createAsteroid()
		}
	}
}