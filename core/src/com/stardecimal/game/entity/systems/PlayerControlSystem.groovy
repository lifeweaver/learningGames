package com.stardecimal.game.entity.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.stardecimal.game.GameJamGame
import com.stardecimal.game.LevelFactory
import com.stardecimal.game.entity.components.PlayerComponent
import com.stardecimal.game.entity.components.SdBodyComponent
import com.stardecimal.game.entity.util.Mapper
import com.stardecimal.game.util.DFUtils
import com.stardecimal.game.util.KeyboardController

class PlayerControlSystem extends IteratingSystem {
	KeyboardController controller
	LevelFactory levelFactory
	float speed = 1
	long lastBounce = System.currentTimeMillis()
	long lastScore = System.currentTimeMillis()
	long lastClick = System.currentTimeMillis()
	OrthographicCamera camera

	@SuppressWarnings("unchecked")
	PlayerControlSystem(LevelFactory lvlFactory, OrthographicCamera cam) {
		super(Family.all(PlayerComponent.class).get())
		controller = lvlFactory.controller
		levelFactory = lvlFactory
		camera = cam
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		SdBodyComponent playerBody = Mapper.bCom.get(entity)
		//Update the invulnerabilityTime for the player
		playerBody.invulnerabilityTime -= deltaTime

		if(playerBody.body.position.y < 0) {
			levelFactory.parent.parent.state = GameJamGame.STATE.OVER
			return
		}

		if(playerBody.body.position.x % (levelFactory.lastPlatformX / RenderingSystem.PPM / 20) < 0.1 && System.currentTimeMillis() - lastScore > 1000) {
			lastScore = System.currentTimeMillis()
			levelFactory.playerScore += 100
		}

		if (controller.a) {
			playerBody.body.setLinearVelocity(MathUtils.lerp(playerBody.body.linearVelocity.x, -speed, 0.2f), playerBody.body.linearVelocity.y)
		}

		if (controller.d) {
			playerBody.body.setLinearVelocity(MathUtils.lerp(playerBody.body.linearVelocity.x, +speed, 0.2f), playerBody.body.linearVelocity.y)
		}

		if (controller.spacbar && System.currentTimeMillis() - lastBounce > 1000) {
			lastBounce = System.currentTimeMillis()
			playerBody.body.applyLinearImpulse(new Vector2(0, 0.2), playerBody.body.position, true)
		}

		if(controller.isMouse1Down && System.currentTimeMillis() - lastClick > 200 ) {
			lastClick = System.currentTimeMillis()
			Vector3 gameCoords = camera.unproject(new Vector3(controller.mouseLocation.x, controller.mouseLocation.y, 0))
			Vector2 pos = new Vector2(gameCoords.x, gameCoords.y)
			float angle = DFUtils.vectorToAngle(DFUtils.aimTo(playerBody.body.position, pos))
			levelFactory.createShot(playerBody.body.position, angle)
		}

		if (!controller.a && !controller.d) {
			playerBody.body.setLinearVelocity(MathUtils.lerp(playerBody.body.linearVelocity.x, 0, 0.2f), playerBody.body.linearVelocity.y)
		}

		playerBody.body.setLinearVelocity(playerBody.body.linearVelocity.x, MathUtils.lerp(playerBody.body.linearVelocity.y, 0, 0.2f))
	}
}