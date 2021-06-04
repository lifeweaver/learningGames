package net.stardecimal.game.pacman.entity.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.math.Vector2
import net.stardecimal.game.KeyboardController
import net.stardecimal.game.entity.components.Mapper
import net.stardecimal.game.entity.components.PlayerComponent
import net.stardecimal.game.entity.components.SdBodyComponent
import net.stardecimal.game.entity.systems.RenderingSystem
import net.stardecimal.game.pacman.LevelFactory

class PlayerControlSystem extends IteratingSystem {
	KeyboardController controller
	LevelFactory levelFactory
	float tileHeight
	float tileWidth
	float offsetX

	@SuppressWarnings("unchecked")
	PlayerControlSystem(LevelFactory lvlFactory) {
		super(Family.all(PlayerComponent.class).get())
		controller = lvlFactory.controller
		levelFactory = lvlFactory
		tileHeight = levelFactory.collisionLayer.tileHeight * RenderingSystem.PIXELS_TO_METRES as float
		tileWidth = levelFactory.collisionLayer.tileWidth * RenderingSystem.PIXELS_TO_METRES as float
		offsetX = levelFactory.collisionLayer.offsetX / (1 / RenderingSystem.PIXELS_TO_METRES)
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		SdBodyComponent playerBody = Mapper.bCom.get(entity)

		if (controller.left && shouldChangeVelocity(playerBody, deltaTime)) {
			Mapper.texCom.get(entity).animation.playMode = Animation.PlayMode.LOOP
			playerBody.body.setLinearVelocity(-3f, 0)
			Mapper.transCom.get(entity).rotation = 0
		}

		if (controller.right && shouldChangeVelocity(playerBody)) {
			Mapper.texCom.get(entity).animation.playMode = Animation.PlayMode.LOOP
			playerBody.body.setLinearVelocity(+3f, 0)
			Mapper.transCom.get(entity).rotation = 180
		}

		if (controller.up && shouldChangeVelocity(playerBody)) {
			Mapper.texCom.get(entity).animation.playMode = Animation.PlayMode.LOOP
			playerBody.body.setLinearVelocity(0, +3f)
			Mapper.transCom.get(entity).rotation = 270
		}

		if (controller.down && shouldChangeVelocity(playerBody)) {
			Mapper.texCom.get(entity).animation.playMode = Animation.PlayMode.LOOP
			playerBody.body.setLinearVelocity(0, -3f)
			Mapper.transCom.get(entity).rotation = 90
		}
		if(controller.spacbar) {
			playerBody.body.setLinearVelocity(0, 0)
		}
	}

	boolean shouldChangeVelocity(SdBodyComponent body, float deltaTime=0) {
		Vector2 tilePos = levelFactory.tilePosition(body.body.position.x, body.body.position.y)
		switch (true) {
			case controller.left:
				def nextTileCenter = levelFactory.gamePosition(tilePos.x - 1 as int, tilePos.y as int).y
				if(body.body.linearVelocity.y != 0 && Math.abs(nextTileCenter - body.body.position.y) > 0.06) {
					return false
				}
				tilePos.x = tilePos.x - 1 as float
				break

			case controller.right:
				def nextTileCenter = levelFactory.gamePosition(tilePos.x + 1 as int, tilePos.y as int).y
				if(body.body.linearVelocity.y != 0 && Math.abs(nextTileCenter - body.body.position.y) > 0.06) {
					return false
				}
				tilePos.x = tilePos.x + 1 as float
				break

			case controller.up:
				def nextTileCenter = levelFactory.gamePosition(tilePos.x as int, tilePos.y + 1 as int).x
				if(body.body.linearVelocity.x != 0 && Math.abs(nextTileCenter - body.body.position.x) > 0.03) {
					return false
				}
				tilePos.y = tilePos.y + 1 as float
				break

			case controller.down:
				def nextTileCenter = levelFactory.gamePosition(tilePos.x as int, tilePos.y - 1 as int).x
				if(body.body.linearVelocity.x != 0 && Math.abs(nextTileCenter - body.body.position.x) > 0.03) {
					return false
				}
				tilePos.y = tilePos.y - 1 as float
				break
		}

		return !levelFactory.isCellBlocked(tilePos)
	}
}
