package net.stardecimal.game.asteroids.entity.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.Vector2
import net.stardecimal.game.DFUtils
import net.stardecimal.game.KeyboardController
import net.stardecimal.game.asteroids.LevelFactory
import net.stardecimal.game.entity.components.Mapper
import net.stardecimal.game.entity.components.ParticleEffectComponent
import net.stardecimal.game.entity.components.PlayerComponent
import net.stardecimal.game.entity.components.SdBodyComponent
import net.stardecimal.game.entity.components.VelocityComponent

class PlayerControlSystem extends IteratingSystem {
	KeyboardController controller
	LevelFactory levelFactory
	long lastSpaceBar = System.currentTimeMillis()
	long lastMovement = System.currentTimeMillis()
	float radians = 0
	float rotationSpeed = 6
	float acceleration = 60
	float deceleration = 0.01
	float maxSpeed = 10

	@SuppressWarnings("unchecked")
	PlayerControlSystem(LevelFactory lvlFactory) {
		super(Family.all(PlayerComponent.class).get())
		controller = lvlFactory.controller
		levelFactory = lvlFactory
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		SdBodyComponent playerBody = Mapper.bCom.get(entity)
		VelocityComponent velCom = Mapper.velCom.get(entity)
		//Update the invulnerabilityTime for the player
		playerBody.invulnerabilityTime -= deltaTime

		float currentSpeed = Math.sqrt(velCom.linearVelocity.x * velCom.linearVelocity.x + velCom.linearVelocity.y * velCom.linearVelocity.y) as float

		if(controller.spacbar && System.currentTimeMillis() - lastSpaceBar > 300) {
			lastSpaceBar = System.currentTimeMillis()
			println("player fired")
			levelFactory.playerShoot()
		}

		if (controller.left) {
			radians += rotationSpeed * deltaTime
			playerBody.body.setTransform(playerBody.body.position.x, playerBody.body.position.y, radians)
		}

		if (controller.right) {
			radians -= rotationSpeed * deltaTime
			playerBody.body.setTransform(playerBody.body.position.x, playerBody.body.position.y, radians)
		}

		if(System.currentTimeMillis() - lastMovement > 50) {
			lastMovement = System.currentTimeMillis()
			ParticleEffectComponent pec = Mapper.peCom.get(entity)
			if (controller.up) {
				Vector2 newVelocity = new Vector2()
				DFUtils.angleToVector(newVelocity, radians)

				velCom.linearVelocity.x += newVelocity.x * acceleration * deltaTime as float
				velCom.linearVelocity.y += newVelocity.y * acceleration * deltaTime as float
				currentSpeed = Math.sqrt(velCom.linearVelocity.x * velCom.linearVelocity.x + velCom.linearVelocity.y * velCom.linearVelocity.y) as float

				//Speed limit enforcement
				if(currentSpeed > maxSpeed) {
					velCom.linearVelocity.x = (velCom.linearVelocity.x / currentSpeed) * maxSpeed as float
					velCom.linearVelocity.y = (velCom.linearVelocity.y / currentSpeed) * maxSpeed as float
				}

				//Show flames
				if(pec.destroyExistingParticles) {
					pec.destroyExistingParticles = false
				}
			} else {
				//Remove flames
				if(!pec.destroyExistingParticles) {
					pec.destroyExistingParticles = true
				}
			}

		}

		//Deceleration
		if(currentSpeed > 0) {
			velCom.linearVelocity.x -= (velCom.linearVelocity.x / currentSpeed) * deceleration as float
			velCom.linearVelocity.y -= (velCom.linearVelocity.y / currentSpeed) * deceleration as float
		}

	}
}