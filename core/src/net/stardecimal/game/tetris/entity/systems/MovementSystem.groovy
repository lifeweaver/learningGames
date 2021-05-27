package net.stardecimal.game.tetris.entity.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Array
import net.stardecimal.game.entity.components.Mapper
import net.stardecimal.game.entity.components.TransformComponent
import net.stardecimal.game.tetris.LevelFactory
import net.stardecimal.game.tetris.entity.components.ActiveComponent

class MovementSystem extends IteratingSystem {
	private LevelFactory levelFactory
	private Array<Entity> bodiesQueue
	private static float accumulator = 0f
	private static int baseMoveRate = 1
	private static int baseStepTime = 1


	@SuppressWarnings('uncheck')
	MovementSystem(LevelFactory lvlFactory) {
		super(Family.all(TransformComponent.class, ActiveComponent.class).get())
		this.bodiesQueue = new Array<Entity>()
		levelFactory = lvlFactory
	}

	@Override
	void update(float deltaTime) {
		super.update(deltaTime)
		float frameTime = Math.min(deltaTime, 0.25f)
		accumulator += frameTime

		//This keeps things running on a certain schedule
		float stepTime = baseStepTime - (baseStepTime * (levelFactory.fallingRateIncrease + levelFactory.speedUp))as float
		if(accumulator >= stepTime) {
			accumulator -= stepTime

			bodiesQueue.each {
				TransformComponent transCom = Mapper.transCom.get(it)
				if(!transCom) {
					return
				}

				Vector2 bottomLeft = levelFactory.determineBottomLeft(transCom, Mapper.texCom.get(it))

				//If hits what I'm calling the floor
				if(bottomLeft.y <= levelFactory.gridBottom) {
					levelFactory.collision(it)
					return
				}

				transCom.position.y -= baseMoveRate

				if(!levelFactory.isValidMove(it)) {
					//Must have gone too far, we have overlap.
					transCom.position.y += baseMoveRate

					levelFactory.collision(it)
					return
				}
			}
			bodiesQueue.clear()
		}
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		if(!bodiesQueue.contains(entity)) {
			bodiesQueue.add(entity)
		}
	}
}
