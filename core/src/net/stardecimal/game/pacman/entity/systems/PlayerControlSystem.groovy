package net.stardecimal.game.pacman.entity.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import net.stardecimal.game.KeyboardController
import net.stardecimal.game.entity.components.Mapper
import net.stardecimal.game.entity.components.PlayerComponent
import net.stardecimal.game.entity.components.SdBodyComponent
import net.stardecimal.game.pacman.LevelFactory

class PlayerControlSystem extends IteratingSystem {
	KeyboardController controller
	LevelFactory levelFactory

	@SuppressWarnings("unchecked")
	PlayerControlSystem(LevelFactory lvlFactory) {
		super(Family.all(PlayerComponent.class).get())
		controller = lvlFactory.controller
		levelFactory = lvlFactory
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		SdBodyComponent playerBody = Mapper.bCom.get(entity)

		if (controller.left) {
			playerBody.body.setLinearVelocity(-3f, 0)
			Mapper.transCom.get(entity).rotation = 0
		}

		if (controller.right) {
			playerBody.body.setLinearVelocity(+3f, 0)
			Mapper.transCom.get(entity).rotation = 180
		}

		if (controller.up) {
			playerBody.body.setLinearVelocity(0, +4f)
			Mapper.transCom.get(entity).rotation = 270
		}

		if (controller.down) {
			playerBody.body.setLinearVelocity(0, -4f)
			Mapper.transCom.get(entity).rotation = 90
		}
	}

}
