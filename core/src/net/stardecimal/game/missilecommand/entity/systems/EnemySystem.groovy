package net.stardecimal.game.missilecommand.entity.systems

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.ai.steer.SteeringBehavior
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.utils.Array
import net.stardecimal.game.DFUtils
import net.stardecimal.game.ai.SteeringPresets
import net.stardecimal.game.entity.components.Mapper
import net.stardecimal.game.entity.components.SdBodyComponent
import net.stardecimal.game.entity.components.SdLocation
import net.stardecimal.game.entity.components.StateComponent
import net.stardecimal.game.entity.components.SteeringComponent
import net.stardecimal.game.entity.components.TypeComponent
import net.stardecimal.game.missilecommand.LevelFactory
import net.stardecimal.game.missilecommand.entity.components.EnemyComponent

class EnemySystem extends IteratingSystem {
	private LevelFactory levelFactory
	private Array<Entity> enemyQueue
	static final ComponentMapper<EnemyComponent> enemyCom = ComponentMapper.getFor(EnemyComponent.class)

	@SuppressWarnings("unchecked")
	EnemySystem(LevelFactory lvlFactory){
		super(Family.all(EnemyComponent.class).get())
		this.levelFactory = lvlFactory
		enemyQueue = new Array<Entity>()
		priority = levelFactory.engine.getSystem(EnemySpawningSystem).priority + 1
	}

	@Override
	void update(float deltaTime) {
		super.update(deltaTime)

		enemyQueue.each {
			TypeComponent type = Mapper.typeCom.get(it)

			if(type.type == TypeComponent.TYPES.SMART_BOMB) {
				StateComponent stateCom = Mapper.stateCom.get(it)
				SteeringComponent scom = Mapper.sCom.get(it)

				//Get closest explosion
				def closestExplosion = closestThreat(Mapper.bCom.get(it).body.position)

				if(closestExplosion) {
					Vector2 pos = Mapper.bCom.get(closestExplosion).body.position
					if(Mapper.bCom.get(it).body.position.dst(pos) < 2) {
						SdLocation fleeFrom = new SdLocation(position: pos, orientation: 0)

						SteeringBehavior<Vector2> steeringBehavior = SteeringPresets.getFlee(scom, fleeFrom)
						scom.maxLinearSpeed = 1f
						scom.steeringBehavior = steeringBehavior
						stateCom.state = StateComponent.STATE_FLEEING
					}
				} else {
					if(stateCom.state == StateComponent.STATE_FLEEING) {
						stateCom.state = StateComponent.STATE_MOVING
						SdLocation target = new SdLocation(position: Mapper.bCom.get(enemyCom.get(it).target).body.position, orientation: 0)
						SteeringBehavior<Vector2> steeringBehavior = SteeringPresets.getArrive(scom, target)
						scom.maxLinearSpeed = 10f
						scom.steeringBehavior = steeringBehavior
					}
				}
			} else {
				EnemyComponent ecom = enemyCom.get(it)
				if(ecom.fireDelay > 0) {
					ecom.fireDelay = ecom.fireDelay - deltaTime as float
				}

				if(ecom.fireDelay <= 0) {
					SdBodyComponent sdBody = Mapper.bCom.get(it)
					int chance = levelFactory.rand.nextInt(100)

					if (chance > 50 && shouldFire(sdBody.body)) {
						ecom.lastTimeFired = System.currentTimeMillis()
						ecom.missilesFired++
						ecom.fireDelay = ecom.firingDelay
						List<Entity> targets = findTargets()

						if(targets.size()) {
							Collections.shuffle(targets)
							Entity target = targets.first() as Entity
							Vector2 missileStart = new Vector2(sdBody.body.position.x, sdBody.body.position.y)
							Vector2 targetPos = Mapper.bCom.get(target).body.position
							Vector2 aimedVector = DFUtils.aimTo(missileStart, targetPos)
							float angleDeg = DFUtils.vectorToAngle2(aimedVector) * MathUtils.radiansToDegrees as float
//						    println("missileStart: ${missileStart}, target: ${targetPos}, angle: ${angleDeg}")

							levelFactory.createEnemyMissile(missileStart, angleDeg)
						}
					}
				}
			}
		}

		enemyQueue.clear()
	}

	private List<Entity> findTargets() {
		return engine.getEntities().findAll {
			Mapper.typeCom.get(it)?.type == TypeComponent.TYPES.CITY || Mapper.typeCom.get(it)?.type == TypeComponent.TYPES.DEFENDER_MISSILE
		}
	}

	private Entity closestThreat(Vector2 target, int type=TypeComponent.TYPES.EXPLOSION) {
		return engine.getEntities().findAll {
			Mapper.typeCom.get(it)?.type == type
		}.sort { Entity entity ->
			Mapper.bCom.get(entity).body.position.dst(target)
		}[0]
	}

	private static boolean shouldFire(Body body) {
		boolean isGoingRight = body.linearVelocity.x > 0
		return ((body.position.x > 5 && isGoingRight) || (body.position.x < 35 && !isGoingRight))
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		if(entity && [TypeComponent.TYPES.BOMBER_PLANE, TypeComponent.TYPES.SATELLITE, TypeComponent.TYPES.SMART_BOMB].contains(Mapper.typeCom.get(entity).type)) {
			enemyQueue.add(entity)
		}
	}
}
