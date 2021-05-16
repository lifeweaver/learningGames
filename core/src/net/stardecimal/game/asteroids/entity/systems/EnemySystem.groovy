package net.stardecimal.game.asteroids.entity.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.utils.Array
import net.stardecimal.game.asteroids.LevelFactory
import net.stardecimal.game.entity.components.EnemyComponent

class EnemySystem extends IteratingSystem {
	LevelFactory levelFactory
	private Array<Entity> enemyQueue

	@SuppressWarnings("unchecked")
	EnemySystem(LevelFactory lvlFactory) {
		super(Family.all(EnemyComponent.class).get())
		levelFactory = lvlFactory
		enemyQueue = new Array<Entity>()
	}

	@Override
	void update(float deltaTime) {
		super.update(deltaTime)
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		enemyQueue.add(entity)
	}
}
