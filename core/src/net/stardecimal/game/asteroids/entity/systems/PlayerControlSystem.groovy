package net.stardecimal.game.asteroids.entity.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import net.stardecimal.game.KeyboardController
import net.stardecimal.game.asteroids.LevelFactory
import net.stardecimal.game.entity.components.Mapper
import net.stardecimal.game.entity.components.PlayerComponent
import net.stardecimal.game.entity.components.SdBodyComponent
import net.stardecimal.game.entity.systems.RenderingSystem

class PlayerControlSystem extends IteratingSystem {
	KeyboardController controller
	LevelFactory levelFactory
	long lastSpaceBar = System.currentTimeMillis()
	float radians = 3.1415f / 2
	float rotationSpeed = 3
	float dx = 0
	float dy = 0
	float acceleration = 200
	float deceleration = 10
	float maxSpeed = 300
	float acceleratingTimer = 0

	@SuppressWarnings("unchecked")
	PlayerControlSystem(KeyboardController keyCon, LevelFactory lvlFactory) {
		super(Family.all(PlayerComponent.class).get())
		controller = keyCon
		levelFactory = lvlFactory
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		SdBodyComponent playerBody = Mapper.bCom.get(entity)

		if(controller.spacbar && System.currentTimeMillis() - lastSpaceBar > 500) {
			lastSpaceBar = System.currentTimeMillis()
			println("player fired")
			levelFactory.playerShoot()
		}

		if (controller.left) {
			radians += rotationSpeed * deltaTime
//			playerBody.body.setTransform(playerBody.body.position.x, playerBody.body.position.y, playerBody.body.angle + 0.1 as float)
		}

		if (controller.right) {
			radians -= rotationSpeed * deltaTime
//			playerBody.body.setTransform(playerBody.body.position.x, playerBody.body.position.y, playerBody.body.angle - 0.1 as float)
		}

		if (controller.up) {
			dx += MathUtils.cos(radians) * acceleration * deltaTime
			dy += MathUtils.cos(radians) * acceleration * deltaTime
			acceleratingTimer += deltaTime
			if(acceleratingTimer > 0.1f) {
				acceleratingTimer = 0
			}
//			playerBody.body.setLinearVelocity(playerBody.body.linearVelocity.x, MathUtils.lerp(playerBody.body.linearVelocity.y, 5, 0.2f))
		} else {
			acceleratingTimer = 0
		}

		float vec = (float) Math.sqrt(dx * dx + dy * dy)
		if(vec > 0) {
			dx -= (dx / vec) * deceleration * deltaTime
			dy -= (dy / vec) * deceleration * deltaTime
		} else if(vec > maxSpeed) {
			dx = (dx / vec) * maxSpeed as float
			dy = (dy / vec) * maxSpeed as float
		}

		float playerX = playerBody.body.linearVelocity.x
		float playerY = playerBody.body.linearVelocity.y
		playerX += dx * dx
		playerY += dy * dy

		playerBody.body.setLinearVelocity(playerX, playerY)
	}
}