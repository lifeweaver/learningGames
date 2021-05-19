package net.stardecimal.game.asteroids.entity.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.ai.steer.SteeringBehavior
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Array
import net.stardecimal.game.DFUtils
import net.stardecimal.game.ai.SteeringPresets
import net.stardecimal.game.asteroids.LevelFactory
import net.stardecimal.game.entity.components.BulletComponent
import net.stardecimal.game.entity.components.EnemyComponent
import net.stardecimal.game.entity.components.Mapper
import net.stardecimal.game.entity.components.SdLocation
import net.stardecimal.game.entity.components.StateComponent
import net.stardecimal.game.entity.components.SteeringComponent
import net.stardecimal.game.entity.components.TypeComponent

class EnemySystem extends IteratingSystem {
	LevelFactory levelFactory
	private Array<Entity> enemyQueue
	static float enemySpawningInterval = 15
	float lastEnemySpawned = 0
	static float enemyShootingInterval = 3
	float lastShot = enemyShootingInterval

	@SuppressWarnings("unchecked")
	EnemySystem(LevelFactory lvlFactory) {
		super(Family.all(EnemyComponent.class).get())
		levelFactory = lvlFactory
		enemyQueue = new Array<Entity>()
	}

	@Override
	void update(float deltaTime) {
		super.update(deltaTime)
		lastEnemySpawned = lastEnemySpawned > 0 ? lastEnemySpawned - deltaTime as float : lastEnemySpawned
		lastShot = lastShot > 0 ? lastShot - deltaTime as float : lastShot

		if(levelFactory.player) {
			if(enemyQueue.isEmpty() && lastEnemySpawned <= 0) {
				lastEnemySpawned = enemySpawningInterval
				println("created Enemy")
				levelFactory.createEnemy()
			}

			enemyQueue.each {
				SteeringComponent scom = Mapper.sCom.get(it)
				StateComponent stateCom = Mapper.stateCom.get(it)
				if(scom) {
					def closestAsteroid = closestThreat(Mapper.bCom.get(it).body.position)
					Vector2 pos = Mapper.bCom.get(closestAsteroid).body.position

					if(closestAsteroid && Mapper.bCom.get(it).body.position.dst(pos) < 2) {
						SdLocation fleeFrom = new SdLocation(position: pos, orientation: 0)

						SteeringBehavior<Vector2> steeringBehavior = SteeringPresets.getFlee(scom, fleeFrom)
						scom.maxLinearSpeed = 5f
						scom.steeringBehavior = steeringBehavior
						stateCom.state = StateComponent.STATE_FLEEING
					} else {
						if(stateCom.state == StateComponent.STATE_FLEEING) {
							stateCom.state = StateComponent.STATE_MOVING
							SdLocation target = new SdLocation(position: Mapper.bCom.get(levelFactory.player).body.position, orientation: 0)
							SteeringBehavior<Vector2> steeringBehavior = SteeringPresets.getArrive(scom, target)
							scom.maxLinearSpeed = 5f
							scom.steeringBehavior = steeringBehavior
						}
					}

					//Shoot at player
					if(lastShot <= 0) {
						lastShot = enemyShootingInterval
						Vector2 playerPos = Mapper.bCom.get(levelFactory.player).body.position
						Vector2 enemyPos = Mapper.bCom.get(it).body.position
						float shootingAngle = DFUtils.vectorToAngle(DFUtils.aimTo(enemyPos, playerPos))
						levelFactory.createShot(enemyPos, shootingAngle, BulletComponent.Owner.ENEMY)
					}
				}
			}
		}
		enemyQueue.clear()
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		enemyQueue.add(entity)
	}

	private Entity closestThreat(Vector2 target, int[] types=[TypeComponent.TYPES.ASTEROID, TypeComponent.TYPES.MEDIUM_ASTEROID, TypeComponent.TYPES.MINI_ASTEROID, TypeComponent.TYPES.BULLET]) {
		return engine.getEntities().findAll {
			types.contains(Mapper.typeCom.get(it)?.type)
		}.sort { Entity entity ->
			Mapper.bCom.get(entity).body.position.dst(target)
		}[0]
	}
}
