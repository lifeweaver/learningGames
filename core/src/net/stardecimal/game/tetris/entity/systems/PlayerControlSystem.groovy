package net.stardecimal.game.tetris.entity.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Vector2
import net.stardecimal.game.KeyboardController
import net.stardecimal.game.entity.components.Mapper
import net.stardecimal.game.entity.components.TextureComponent
import net.stardecimal.game.entity.components.TransformComponent
import net.stardecimal.game.entity.systems.RenderingSystem
import net.stardecimal.game.tetris.LevelFactory
import net.stardecimal.game.entity.components.PlayerComponent
import net.stardecimal.game.tetris.entity.components.BlockComponent

class PlayerControlSystem extends IteratingSystem {
	LevelFactory levelFactory
	KeyboardController controller
	long lastKey = System.currentTimeMillis()
	Vector2 screenSize
	float maxX
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
		TransformComponent transCom = Mapper.transCom.get(entity)
		screenSize = RenderingSystem.getScreenSizeInMeters()
		maxX = levelFactory.gridWidth

		if(controller.left && System.currentTimeMillis() - lastKey > 100) {
			move(entity, transCom, false)
		}

		if(controller.right && System.currentTimeMillis() - lastKey > 100) {
			move(entity, transCom)
		}

		if(controller.up && System.currentTimeMillis() - lastKey > 200) {
			rotate(entity, transCom)
		}

		if(controller.down && System.currentTimeMillis() - lastKey > 200) {
			rotate(entity, transCom, false)
		}

		if(!controller.spacbar) {
			levelFactory.speedUp = 0
		}

		if(controller.spacbar && System.currentTimeMillis() - lastKey > 100) {
			lastKey = System.currentTimeMillis()
			levelFactory.speedUp = 0.75
		}
	}

	void rotate(Entity entity, TransformComponent playerBody, boolean isRight=true) {
		lastKey = System.currentTimeMillis()
		TextureComponent texCom = Mapper.texCom.get(entity)
		float rotation = isRight ? 90 : -90
		float startAngle = playerBody.rotation
		float angle = playerBody.rotation + rotation as float
		playerBody.rotation = angle % 360

		//Check if the rotation violated the movement.
		if(!levelFactory.isValidMove(entity)) {
			playerBody.rotation = startAngle
			return
		}

		//Change texture offsets to account for the rotation
		int testValue = Math.abs(playerBody.rotation) as int
		BlockComponent.BlockType blockType = levelFactory.blockMapper.get(entity).type
		if(blockType == BlockComponent.BlockType.I) {
			if(testValue == 90 || testValue == 270) {
				texCom.offsetY = 0.5
				texCom.offsetX = 0
			} else {
				texCom.offsetY = texCom.initialOffsetY
				texCom.offsetX = texCom.initialOffsetX
			}
		}

		if(BlockComponent.threeWideBlocks.contains(blockType)) {
			if(testValue == 90 || testValue == 270) {
				texCom.offsetY = 0.5
				texCom.offsetX = 0
			} else {
				texCom.offsetY = texCom.initialOffsetY
				texCom.offsetX = texCom.initialOffsetX
			}
		}
	}

	void move(Entity entity, TransformComponent playerBody, boolean isRight=true) {
		lastKey = System.currentTimeMillis()
		int difference = isRight ? 1 : -1
		float startX = playerBody.position.x
		float blockStart = levelFactory.adjustedWidth(entity, false)
		float blockEnd = levelFactory.adjustedWidth(entity)

		if(isRight && blockEnd == maxX as float) {
			difference = 0
		} else if(!isRight && blockStart <= 0) {
			difference = 0
		}

		playerBody.position.x = startX + difference as float

		if(!levelFactory.isValidMove(entity)) {
			playerBody.position.x = startX
			return
		}
	}
}
