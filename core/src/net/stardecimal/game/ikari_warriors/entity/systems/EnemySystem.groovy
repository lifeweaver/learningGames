package net.stardecimal.game.ikari_warriors.entity.systems

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.utils.Array
import net.stardecimal.game.DFUtils
import net.stardecimal.game.entity.components.BulletComponent
import net.stardecimal.game.entity.components.Mapper
import net.stardecimal.game.entity.components.SdBodyComponent
import net.stardecimal.game.entity.components.SteeringComponent
import net.stardecimal.game.entity.components.TypeComponent
import net.stardecimal.game.entity.systems.RenderingSystem
import net.stardecimal.game.ikari_warriors.LevelFactory
import net.stardecimal.game.ikari_warriors.entity.components.EnemyComponent

class EnemySystem extends IteratingSystem {
	private LevelFactory levelFactory
	private Array<Entity> enemyQueue
	static final ComponentMapper<EnemyComponent> enemyCom = ComponentMapper.getFor(EnemyComponent.class)

	@SuppressWarnings("unchecked")
	EnemySystem(LevelFactory lvlFactory){
		super(Family.all(EnemyComponent.class).get())
		this.levelFactory = lvlFactory
		priority = levelFactory.engine.getSystem(EnemySpawningSystem).priority + 1
		enemyQueue = new Array<Entity>()
	}

	@Override
	void update(float deltaTime) {
		super.update(deltaTime)

		enemyQueue.each {Entity entity ->
			TypeComponent type = Mapper.typeCom.get(entity)
			EnemyComponent enemyComponent = enemyCom.get(entity)
			if(enemyComponent.firingDelay > 0) {
				enemyComponent.firingDelay -= deltaTime
			}

			if(type.type == TypeComponent.TYPES.GUN_SOLDIER) {
				gunSoldierBehavior(entity, enemyComponent)
			}
		}

		enemyQueue.clear()
	}

	void gunSoldierBehavior(Entity entity, EnemyComponent enemyComponent) {
		SdBodyComponent playerBody = Mapper.bCom.get(levelFactory.player)
		SdBodyComponent sdBody = Mapper.bCom.get(entity)
		Vector2 soldierPos = sdBody.body.position
		SteeringComponent scom = Mapper.sCom.get(entity)

		//Movement
		if(playerBody) {
			//Once a soldier is in position, stop and return fire
			if(scom.currentMode == SteeringComponent.SteeringState.SEEK && soldierPos.dst(playerBody.body.position) <= 15) {
				scom.currentMode = SteeringComponent.SteeringState.NONE
				scom.steeringBehavior = null
				sdBody.body.setLinearVelocity(0, 0)
			}


			//Shooting - only shoot when not moving, and ready to shoot
			if(scom.currentMode == SteeringComponent.SteeringState.NONE && enemyComponent.firingDelay <= 0) {
				if(playerBody && foundShootablePlayer(soldierPos, playerBody.body.position)) {
					float shootingAngle = DFUtils.vectorToAngle(DFUtils.aimTo(soldierPos, playerBody.body.position))

					if(enemyComponent.timesFired > 2) {
						enemyComponent.firingDelay = enemyComponent.FIRING_INTERVAL
						enemyComponent.lastTimeFired = System.currentTimeMillis()
						enemyComponent.timesFired = 0
					} else {
						enemyComponent.timesFired++
						enemyComponent.firingDelay = enemyComponent.FIRING_INTERVAL_BURST
					}

					//If the enemy hasn't shot yet, give them a chance to throw a grenade instead
					if(enemyComponent.timesFired == 1 && levelFactory.rand.nextInt(100) > 95) {
						levelFactory.createGrenade(soldierPos, shootingAngle, BulletComponent.Owner.ENEMY)
						enemyComponent.firingDelay = enemyComponent.FIRING_INTERVAL
						enemyComponent.lastTimeFired = System.currentTimeMillis()
						enemyComponent.timesFired = 0
					} else {
						levelFactory.createShot(soldierPos, shootingAngle, BulletComponent.Owner.ENEMY)
					}
				} else {
					//Reset
					enemyComponent.timesFired = 0
				}
			}
		}
	}


	private boolean foundShootablePlayer(Vector2 soldierPos, Vector2 playerPos) {
		Array<Body> entitiesHitByExplosion = levelFactory.circleRayCast(soldierPos, RenderingSystem.getScreenSizeInPixesWorld().x / 2 as float, 8)
		float shootingAngle = 0
		boolean targetAcquired = false
		entitiesHitByExplosion.each {
			boolean test = false
			if(it?.userData instanceof Entity) {
				Entity ent = it.userData as Entity
				if(Mapper.typeCom.get(ent)?.type == TypeComponent.TYPES.PLAYER) {
					targetAcquired = canFire(soldierPos, playerPos)
					if(targetAcquired) {
						shootingAngle = DFUtils.vectorToAngle(DFUtils.aimTo(soldierPos, playerPos))

						//println("shootingAngle: ${MathUtils.radiansToDegrees * shootingAngle}")
						//TODO: rotate to that direction
						//Mapper.transCom.get(entity).rotation
						//Or maybe switch to that texture so it looks like it's aiming?
					} else {
						return targetAcquired
					}
				}
			}
		}
		return targetAcquired
	}

	private boolean canFire(Vector2 firingPos, Vector2 endPos) {
		Array<Body> entitiesHitByRay = levelFactory.singleRayCast(firingPos, endPos)

		boolean alliesBetween = entitiesHitByRay.find {
			if(it?.userData instanceof Entity) {
				Entity ent = it.userData as Entity
				int type = Mapper.typeCom.get(ent).type
				if(![TypeComponent.TYPES.BULLET, TypeComponent.TYPES.PLAYER].contains(type)) {
					return true
				}
			}
			return false
		}


		return !alliesBetween
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		enemyQueue.add(entity)
	}
}
