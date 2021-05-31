package net.stardecimal.game.pacman.entity.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.Vector2
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

		if (controller.left && shouldChangeVelocity(playerBody)) {
			playerBody.body.setLinearVelocity(-3f, 0)
			Mapper.transCom.get(entity).rotation = 0
		}

		if (controller.right && shouldChangeVelocity(playerBody)) {
			playerBody.body.setLinearVelocity(+3f, 0)
			Mapper.transCom.get(entity).rotation = 180
		}

		if (controller.up && shouldChangeVelocity(playerBody)) {
			playerBody.body.setLinearVelocity(0, +4f)
			Mapper.transCom.get(entity).rotation = 270
		}

		if (controller.down && shouldChangeVelocity(playerBody)) {
			playerBody.body.setLinearVelocity(0, -4f)
			Mapper.transCom.get(entity).rotation = 90
		}
	}

	boolean shouldChangeVelocity(SdBodyComponent body) {
		Vector2 tilePos = levelFactory.tilePosition(body.body.position.x, body.body.position.y)
		switch (true) {
			case controller.left:
				tilePos.x = tilePos.x - 1 as float
				break

			case controller.right:
				tilePos.x = tilePos.x + 1 as float
				break

			case controller.up:
				tilePos.y = tilePos.y + 1 as float
				break

			case controller.down:
				tilePos.y = tilePos.y - 1 as float
				break
		}

		return !levelFactory.isCellBlocked(tilePos)
	}

}
