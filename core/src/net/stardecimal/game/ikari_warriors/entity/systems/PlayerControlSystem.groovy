package net.stardecimal.game.ikari_warriors.entity.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.MathUtils
import net.stardecimal.game.KeyboardController
import net.stardecimal.game.entity.components.Mapper
import net.stardecimal.game.entity.components.PlayerComponent
import net.stardecimal.game.entity.components.SdBodyComponent
import net.stardecimal.game.ikari_warriors.LevelFactory

class PlayerControlSystem extends IteratingSystem {
	KeyboardController controller
	LevelFactory levelFactory
	float speed = 5
	long lastShot = System.currentTimeMillis()
	long lastTurn = System.currentTimeMillis()

	@SuppressWarnings("unchecked")
	PlayerControlSystem(LevelFactory lvlFactory) {
		super(Family.all(PlayerComponent.class).get())
		controller = lvlFactory.controller
		levelFactory = lvlFactory
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		SdBodyComponent playerBody = Mapper.bCom.get(entity)

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
			playerBody.body.setTransform(playerBody.body.position.x, playerBody.body.position.y, playerBody.body.angle + (MathUtils.degreesToRadians * 45) as float)
		}

		if (controller.e && System.currentTimeMillis() - lastTurn > 300) {
			lastTurn = System.currentTimeMillis()
			playerBody.body.setTransform(playerBody.body.position.x, playerBody.body.position.y, playerBody.body.angle - (MathUtils.degreesToRadians * 45) as float)
		}

		if (controller.left) {
			//TODO: grenade
		}

		if (controller.right && System.currentTimeMillis() - lastShot > 300) {
			lastShot = System.currentTimeMillis()
			levelFactory.playerShoot()
		}

		if (!controller.a && !controller.d) {
			playerBody.body.setLinearVelocity(MathUtils.lerp(playerBody.body.linearVelocity.x, 0, 0.2f), playerBody.body.linearVelocity.y)
		}

		if (!controller.w && !controller.s) {
			playerBody.body.setLinearVelocity(playerBody.body.linearVelocity.x, MathUtils.lerp(playerBody.body.linearVelocity.y, 0, 0.2f))
		}
		
	}
}
