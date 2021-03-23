package net.stardecimal.game.pong.entity.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.MathUtils
import net.stardecimal.game.KeyboardController
import net.stardecimal.game.entity.components.Mapper
import net.stardecimal.game.entity.components.PlayerComponent
import net.stardecimal.game.entity.components.SdBodyComponent
import net.stardecimal.game.entity.components.StateComponent

class PongPlayerControlSystem extends IteratingSystem {
	KeyboardController controller

	@SuppressWarnings("unchecked")
	PongPlayerControlSystem(KeyboardController keyCon) {
		super(Family.all(PlayerComponent.class).get())
		controller = keyCon
	}
	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		SdBodyComponent playerBody = Mapper.bCom.get(entity)
		StateComponent state = Mapper.stateCom.get(entity)
		PlayerComponent player = Mapper.playerCom.get(entity)
//		player.cam.position.y = playerBody.body.position.y

//		println("position y: ${playerBody.body.position.y}")
		if(playerBody.body.linearVelocity.y == 0){
			if(playerBody.body.linearVelocity.y != 0 && state.get() != StateComponent.STATE_MOVING){
				state.set(StateComponent.STATE_MOVING)
			}
		}

		if(controller.up) {
			playerBody.body.setLinearVelocity(playerBody.body.linearVelocity.x, MathUtils.lerp(playerBody.body.linearVelocity.y, 10f, 0.2f))
		}

		if(controller.down) {
			playerBody.body.setLinearVelocity(playerBody.body.linearVelocity.x, MathUtils.lerp(playerBody.body.linearVelocity.y, -10f, 0.2f))
		}

		if(!controller.up && !controller.down){
			playerBody.body.setLinearVelocity(playerBody.body.linearVelocity.x, MathUtils.lerp(playerBody.body.linearVelocity.y, 0, 0.1f))
		}
	}
}
