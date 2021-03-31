package net.stardecimal.game.worm.entity.systems

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.Vector2
import net.stardecimal.game.KeyboardController
import net.stardecimal.game.entity.components.Mapper
import net.stardecimal.game.worm.LevelFactory
import net.stardecimal.game.worm.entity.components.PlayerComponent
import net.stardecimal.game.entity.components.SdBodyComponent
import net.stardecimal.game.entity.components.StateComponent
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class PlayerControlSystem extends IteratingSystem {
	private static final Logger log = LoggerFactory.getLogger(LevelFactory)
	KeyboardController controller
	LevelFactory levelFactory
	float speed = 5

	@SuppressWarnings("unchecked")
	PlayerControlSystem(KeyboardController keyCon, LevelFactory lvlFactory) {
		super(Family.all(PlayerComponent.class).get())
		controller = keyCon
		levelFactory = lvlFactory
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		SdBodyComponent playerBody = Mapper.bCom.get(entity)
		PlayerComponent playerComponent = ComponentMapper.getFor(PlayerComponent.class).get(entity)
		StateComponent state = Mapper.stateCom.get(entity)
		Vector2 velocity = playerBody.body.linearVelocity

		if((velocity.y != 0 || velocity.x != 0) && state.get() != StateComponent.STATE_MOVING){
			state.set(StateComponent.STATE_MOVING)
		}
		/**
		 * Thoughts on worm movement:
		 * When a button is pressed, add a delay, like 100 ms
		 * Need to keep the bodies from moving on the other axis
		 * Need to not let the rope connection affect the bodies
		 * When a button is pressed, save the x/y
		 *
		 *
		 */

		double currentAngle = Math.toDegrees(playerBody.body.angle).round()

		/**
		 * Store a list of the pos(Needs converted to tile), and angle of the rotation
		 * Using the current angle, position, length, and rotateList, I should be able to determine where the tail should go
		 * Example 1:
		 * length: 3
		 * angle: 0
		 * position [11,11] //Position should be in the middle of a cell, so odd numbers
		 * rotateList: []
		 * Then 3 cells below me should be part of the tail, which each cell being 2x2
		 * S
		 * |
		 * |
		 * |
		 *
		 * Example 2:
		 * length: 3
		 * angle: 0
		 * position [11,11]
		 * rotateList: [11, 7, 90]
		 * One cell below me should be part of the tail, with two cells one cell down, and two cells to the left of that
		 * being part of the tail
		 *   S
		 * --|
		 *
		 *
		 * The idea is that depending on the length of the tail, and the rotateList, it should draw the tail from the
		 * snake. I think I might have to reverse the rotations to make it look right, I'm not sure.
		 *
		 * Every rotation should switch from incrementing on one axis, like the x or y, to the other.
		 */
		//TODO: modify nextPos so center of body is center of a tile?
		Vector2 nextPos = determineNextPosition(playerBody, deltaTime, currentAngle)
		float newAngle = processInputAndDetermineAngle(playerBody, currentAngle, playerComponent)
		playerBody.body.setTransform(nextPos, newAngle)
		levelFactory.applyBackground(nextPos, currentAngle, playerComponent.length, playerComponent.rotations)
	}

	private Vector2 determineNextPosition(SdBodyComponent playerBody, float deltaTime, double currentAngle) {
		switch(currentAngle) {
			case 0: //Up
				return new Vector2(playerBody.body.position.x.round(1), playerBody.body.position.y + speed * deltaTime as float)
				break

			case 90: //Left
				return new Vector2(playerBody.body.position.x + -speed * deltaTime as float, playerBody.body.position.y.round(1))
				break

			case 180: //Down
				return new Vector2(playerBody.body.position.x.round(1), playerBody.body.position.y + -speed * deltaTime as float)
				break

			case 270: //Right
				return new Vector2(playerBody.body.position.x + speed * deltaTime as float, playerBody.body.position.y.round(1))
				break
		}

		//Never going to happen, there is always an angle
		return null
	}

	private float processInputAndDetermineAngle(SdBodyComponent playerBody, double currentAngle, PlayerComponent playerComponent) {
		switch(true) {
			case controller.up && currentAngle != 180:
				addRotation(playerBody, currentAngle, playerComponent, 0)
				return Math.toRadians(0)
			case controller.down && currentAngle != 0:
				addRotation(playerBody, currentAngle, playerComponent, 180)
				return Math.toRadians(180)
			case controller.left && currentAngle != 270:
				addRotation(playerBody, currentAngle, playerComponent, 90)
				return Math.toRadians(90)
			case controller.right && currentAngle != 90:
				addRotation(playerBody, currentAngle, playerComponent, 270)
				return Math.toRadians(270)
		}

		return Math.toRadians(currentAngle)
	}

	private void addRotation(SdBodyComponent playerBody, double currentAngle, PlayerComponent playerComponent, double newAngle) {
		Vector2 rotation = levelFactory.body2tile(playerBody.body.position.x, playerBody.body.position.y)
		float[] newRotation = [rotation.x, rotation.y, currentAngle, newAngle]
		if(playerComponent.length > 0 && !playerComponent.rotations.any { tooClose(it, newRotation)}) {
			if(currentAngle != newAngle) {
				log.info("Adding rotation")
				playerComponent.rotations << newRotation
			} else {
				log.info("Bad rotation: currentAngle:${currentAngle}, newAngle:${newAngle}")
			}

		}
	}

	private static boolean tooClose(float[] existingRotation, float[] newRotation) {
		return existingRotation[0].round() == newRotation[0].round() && existingRotation[1].round() == newRotation[1].round()
	}
}