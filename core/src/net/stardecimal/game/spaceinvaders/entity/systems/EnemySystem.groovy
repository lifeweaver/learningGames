package net.stardecimal.game.spaceinvaders.entity.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.utils.Array
import net.stardecimal.game.entity.components.EnemyComponent
import net.stardecimal.game.entity.components.Mapper
import net.stardecimal.game.entity.components.SdBodyComponent
import net.stardecimal.game.entity.components.TypeComponent
import net.stardecimal.game.spaceinvaders.LevelFactory

class EnemySystem extends IteratingSystem {
	LevelFactory levelFactory
	private Array<Entity> enemyQueue
	float lastTimeFired = 0
	float fireDelay = 0
	final float firingDelay = 4

	@SuppressWarnings("unchecked")
	EnemySystem(LevelFactory lvlFactory) {
		super(Family.all(EnemyComponent.class).get())
		this.levelFactory = lvlFactory
		enemyQueue = new Array<Entity>()
	}

	@Override
	void update(float deltaTime) {
		super.update(deltaTime)

		if(fireDelay > 0) {
			fireDelay = fireDelay - deltaTime as float
		}

		enemyQueue.each {
			SdBodyComponent sdBody = Mapper.bCom.get(it)
			boolean canFire = this.canFire(sdBody.body.position)

			//can fire, check for allies below
			if(canFire) {
				if(fireDelay <= 0) {
					int chance = levelFactory.rand.nextInt(100)
					if (chance > 90) {
						lastTimeFired = System.currentTimeMillis()
						fireDelay = firingDelay

						levelFactory.createShot(sdBody.body.position, false)
					}
				}
			}
		}

		enemyQueue.clear()
	}

	private boolean canFire(Vector2 firingPos) {
		//Adjust for firing body
		firingPos.y = firingPos.y - 1 as float

		println("firingPos: ${firingPos}, targetPos: ${new Vector2(firingPos.x, 2)}")
		Array<Body> entitiesHitByRay = levelFactory.singleRayCast(firingPos, new Vector2(firingPos.x, 2))

		boolean alliesBelow = entitiesHitByRay.find {
			if(it?.userData instanceof Entity) {
				Entity ent = it.userData as Entity
				return Mapper.typeCom.get(ent).type == TypeComponent.TYPES.ENEMY
			}
			return false
		}


		return !alliesBelow
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		enemyQueue.add(entity)
	}
}
