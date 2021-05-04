package net.stardecimal.game.missilecommand.entity.systems

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.utils.Array
import net.stardecimal.game.DFUtils
import net.stardecimal.game.entity.components.Mapper
import net.stardecimal.game.entity.components.SdBodyComponent
import net.stardecimal.game.entity.components.TypeComponent
import net.stardecimal.game.missilecommand.LevelFactory
import net.stardecimal.game.missilecommand.entity.components.EnemyComponent

class EnemyFiringSystem extends IteratingSystem {
	private LevelFactory levelFactory
	private Array<Entity> enemyQueue
	static final ComponentMapper<EnemyComponent> enemyCom = ComponentMapper.getFor(EnemyComponent.class)

	@SuppressWarnings("unchecked")
	EnemyFiringSystem(LevelFactory lvlFactory){
		super(Family.all(EnemyComponent.class).get())
		this.levelFactory = lvlFactory
		enemyQueue = new Array<Entity>()
		priority = levelFactory.engine.getSystem(EnemySystem).priority + 1
	}

	@Override
	void update(float deltaTime) {
		super.update(deltaTime)

		enemyQueue.each {
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
					List<Entity> targets = engine.getEntities().findAll {
						Mapper.typeCom.get(it)?.type == TypeComponent.TYPES.CITY || Mapper.typeCom.get(it)?.type == TypeComponent.TYPES.DEFENDER_MISSILE
					}

					if(targets.size()) {
						Collections.shuffle(targets)
						Entity target = targets.first() as Entity
						Vector2 missileStart = new Vector2(sdBody.body.position.x, sdBody.body.position.y)
						Vector2 targetPos = Mapper.bCom.get(target).body.position
						Vector2 aimedVector = DFUtils.aimTo(missileStart, targetPos)
						float angleDeg = DFUtils.vectorToAngle2(aimedVector) * MathUtils.radiansToDegrees as float
//						println("missileStart: ${missileStart}, target: ${targetPos}, angle: ${angleDeg}")

						levelFactory.createEnemyMissile(missileStart, angleDeg)
					}
				}
			}

		}

		enemyQueue.clear()
	}

	private static boolean shouldFire(Body body) {
		boolean isGoingRight = body.linearVelocity.x > 0
		return ((body.position.x > 5 && isGoingRight) || (body.position.x < 35 && !isGoingRight))
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		if(entity && [TypeComponent.TYPES.BOMBER_PLANE, TypeComponent.TYPES.SATELLITE].contains(Mapper.typeCom.get(entity).type)) {
			enemyQueue.add(entity)
		}
	}
}
