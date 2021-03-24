package net.stardecimal.game.worm.entity.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import net.stardecimal.game.KeyboardController
import net.stardecimal.game.entity.components.Mapper
import net.stardecimal.game.entity.components.PlayerComponent
import net.stardecimal.game.entity.components.SdBodyComponent
import net.stardecimal.game.entity.components.StateComponent

class PlayerControlSystem extends IteratingSystem {
	KeyboardController controller
	float progress = 0.2f
	float speedPositive = 20
	float speedNegative = -20

	@SuppressWarnings("unchecked")
	PlayerControlSystem(KeyboardController keyCon) {
		super(Family.all(PlayerComponent.class).get())
		controller = keyCon
	}
	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		SdBodyComponent playerBody = Mapper.bCom.get(entity)
		StateComponent state = Mapper.stateCom.get(entity)
		Vector2 velocity = playerBody.body.linearVelocity

		if((velocity.y != 0 || velocity.x != 0) && state.get() != StateComponent.STATE_MOVING){
			state.set(StateComponent.STATE_MOVING)
		}

		//TODO: needs more work to make it more like worm movement
		//TODO: setup so it changes direction based on the last key, right now holding a key higher in the if makes it win.
		// I was really slowing my self down with velocity.x == 0 type stuff here fyi
		if(controller.up) {
			playerBody.body.setLinearVelocity(0, MathUtils.lerp(velocity.y, speedPositive, progress))
		} else if(controller.down) {
			playerBody.body.setLinearVelocity(0, MathUtils.lerp(velocity.y, speedNegative, progress))
		} else if(controller.right) {
			playerBody.body.setLinearVelocity(MathUtils.lerp(velocity.x, speedPositive, progress), 0)
		} else if(controller.left) {
			playerBody.body.setLinearVelocity(MathUtils.lerp(velocity.x, speedNegative, progress), 0)
		}
	}
}