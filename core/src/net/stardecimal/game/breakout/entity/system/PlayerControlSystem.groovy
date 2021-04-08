package net.stardecimal.game.breakout.entity.system

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.MathUtils
import net.stardecimal.game.KeyboardController
import net.stardecimal.game.entity.components.Mapper
import net.stardecimal.game.entity.components.PlayerComponent
import net.stardecimal.game.entity.components.SdBodyComponent
import net.stardecimal.game.entity.components.StateComponent

class PlayerControlSystem extends IteratingSystem {
	KeyboardController controller

	@SuppressWarnings("unchecked")
	PlayerControlSystem(KeyboardController keyCon) {
		super(Family.all(PlayerComponent.class).get())
		controller = keyCon
	}
	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		SdBodyComponent playerBody = Mapper.bCom.get(entity)
		StateComponent state = Mapper.stateCom.get(entity)

		if(playerBody.body.linearVelocity.x == 0){
			if(playerBody.body.linearVelocity.x != 0 && state.get() != StateComponent.STATE_MOVING){
				state.set(StateComponent.STATE_MOVING)
			}
		}

		if(controller.left) {
			playerBody.body.setLinearVelocity(MathUtils.lerp(playerBody.body.linearVelocity.x, -12f, 0.2f), playerBody.body.linearVelocity.y)
		}

		if(controller.right) {
			playerBody.body.setLinearVelocity(MathUtils.lerp(playerBody.body.linearVelocity.x, +12f, 0.2f), playerBody.body.linearVelocity.y)
		}

		if(!controller.left && !controller.right){
			playerBody.body.setLinearVelocity(MathUtils.lerp(playerBody.body.linearVelocity.x, 0, 0.1f), playerBody.body.linearVelocity.y)
		}
	}
}