package com.stardecimal.game.entity.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.MathUtils
import com.stardecimal.game.LevelFactory
import com.stardecimal.game.entity.components.PlayerComponent
import com.stardecimal.game.entity.components.SdBodyComponent
import com.stardecimal.game.entity.util.Mapper
import com.stardecimal.game.util.KeyboardController

class PlayerControlSystem extends IteratingSystem {
	KeyboardController controller
	LevelFactory levelFactory
	float speed = 5
	long lastShot = System.currentTimeMillis()
	long lastGrenade = System.currentTimeMillis()
	long lastTurn = System.currentTimeMillis()
	float rotation = 0

	@SuppressWarnings("unchecked")
	PlayerControlSystem(LevelFactory lvlFactory) {
		super(Family.all(PlayerComponent.class).get())
		controller = lvlFactory.controller
		levelFactory = lvlFactory
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		SdBodyComponent playerBody = Mapper.bCom.get(entity)
		//Update the invulnerabilityTime for the player
		playerBody.invulnerabilityTime -= deltaTime

		if (controller.a) {
			playerBody.body.setLinearVelocity(MathUtils.lerp(playerBody.body.linearVelocity.x, -speed, 0.2f), playerBody.body.linearVelocity.y)
		}

		if (controller.d) {
			playerBody.body.setLinearVelocity(MathUtils.lerp(playerBody.body.linearVelocity.x, +speed, 0.2f), playerBody.body.linearVelocity.y)
		}

		if (controller.w) {
			playerBody.body.setLinearVelocity(playerBody.body.linearVelocity.x, MathUtils.lerp(playerBody.body.linearVelocity.y, +speed, 0.2f))
		}

		if (controller.s) {
			playerBody.body.setLinearVelocity(playerBody.body.linearVelocity.x, MathUtils.lerp(playerBody.body.linearVelocity.y, -speed, 0.2f))
		}

		if (controller.q && System.currentTimeMillis() - lastTurn > 300) {
			lastTurn = System.currentTimeMillis()
			float newAngle = rotation + 45 as float
			rotation = newAngle
//			Mapper.texCom.get(entity).region = levelFactory.determinePlayerTexture(rotation)
		}

		if (controller.e && System.currentTimeMillis() - lastTurn > 300) {
			lastTurn = System.currentTimeMillis()
			float newAngle = rotation - 45 as float
			rotation = newAngle
//			Mapper.texCom.get(entity).region = levelFactory.determinePlayerTexture(rotation)
		}

		if (controller.left && System.currentTimeMillis() - lastGrenade > 1000) {
			lastGrenade = System.currentTimeMillis()
//			levelFactory.playerGrenade(playerBody.body.position, rotation * MathUtils.degreesToRadians as float)
		}

		if (controller.right && System.currentTimeMillis() - lastShot > 300) {
			lastShot = System.currentTimeMillis()
//			levelFactory.playerShoot(rotation * MathUtils.degreesToRadians as float)
		}

		if (!controller.a && !controller.d) {
			playerBody.body.setLinearVelocity(MathUtils.lerp(playerBody.body.linearVelocity.x, 0, 0.2f), playerBody.body.linearVelocity.y)
		}

		if (!controller.w && !controller.s) {
			playerBody.body.setLinearVelocity(playerBody.body.linearVelocity.x, MathUtils.lerp(playerBody.body.linearVelocity.y, 0, 0.2f))
		}

	}
}