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
import net.stardecimal.game.entity.components.TransformComponent
import net.stardecimal.game.entity.components.TypeComponent
import net.stardecimal.game.spaceinvaders.LevelFactory

class EnemySystem extends IteratingSystem {
	LevelFactory levelFactory
	private Array<Entity> enemyQueue
	float lastTimeFired = 0
	float fireDelay = 0
	final float firingDelay = 2
	float lastMovement = 1
	float movementInterval = 1
	boolean goingRight = true
	float change = 0.5
	static float spaceShipInterval = 30
	float lastSpaceShip = spaceShipInterval

	@SuppressWarnings("unchecked")
	EnemySystem(LevelFactory lvlFactory) {
		super(Family.all(EnemyComponent.class).get())
		this.levelFactory = lvlFactory
		enemyQueue = new Array<Entity>()
	}

	@Override
	void update(float deltaTime) {
		super.update(deltaTime)
		float maxX = 0
		float minX = 40

		fireDelay = fireDelay > 0 ? fireDelay - deltaTime as float : fireDelay
		lastMovement = lastMovement > 0 ? lastMovement - deltaTime as float : lastMovement
		lastSpaceShip = lastSpaceShip > 0 ? lastSpaceShip - deltaTime as float : lastSpaceShip
		Entity spaceShip = null

		enemyQueue.each {
			if(Mapper.typeCom.get(it).type == TypeComponent.TYPES.ENEMY_SPACESHIP) {
				spaceShip = it
				return
			}

			SdBodyComponent sdBody = Mapper.bCom.get(it)
			boolean canFire = this.canFire(sdBody.body.position)
			float enemyX = sdBody.body.position.x
			maxX = enemyX > maxX ? enemyX : maxX
			minX = enemyX < minX ? enemyX : minX

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
		enemyQueue.removeValue(spaceShip, true)

		//check if the enemy should move
		if(lastMovement <= 0) {
			lastMovement = movementInterval
			boolean goDown = false

			if(maxX > 38.5) {
				goingRight = false
				goDown = true
			} else if(minX < 1.5) {
				goingRight = true
				goDown = true
			}

			enemyQueue.each {
				Body body = Mapper.bCom.get(it).body
				TransformComponent transCom = Mapper.transCom.get(it)
				float newX = (goingRight ? body.position.x + change : body.position.x - change) as float
				float newY = goDown ? body.position.y - 0.5 as float : body.position.y
				transCom.position.x = newX
				transCom.position.y = newY
				body.setTransform(newX, newY, 0)
			}

		}

		if(spaceShip) {
			lastSpaceShip = spaceShipInterval
			SdBodyComponent sdBody = Mapper.bCom.get(spaceShip)
			if(sdBody && ((sdBody.body.position.x < -5 && sdBody.body.linearVelocity.x < 0) || (sdBody.body.position.x > maxX + 5 && sdBody.body.linearVelocity.x > 0))) {
				sdBody.isDead = true
			}
		} else if(lastSpaceShip <= 0) {
			levelFactory.createEnemy4()
		}

		enemyQueue.clear()
	}

	private boolean canFire(Vector2 firingPos) {
		//Adjust for firing body
		firingPos.y = firingPos.y - 1 as float

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
