package com.stardecimal.game.entity.systems

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.utils.Array
import com.stardecimal.game.entity.util.Mapper
import com.stardecimal.game.entity.components.SdBodyComponent
import com.stardecimal.game.entity.components.TransformComponent
import com.stardecimal.game.entity.components.VelocityComponent

class PhysicsSystem extends IteratingSystem {
	private static final float MAX_STEP_TIME = 1/45f as float
	private static accumulator = 0f

	private World world
	private Array<Entity> bodiesQueue

	private ComponentMapper<SdBodyComponent> bm = ComponentMapper.getFor(SdBodyComponent.class)
	private ComponentMapper<TransformComponent> tm = ComponentMapper.getFor(TransformComponent.class)

	@SuppressWarnings('uncheck')
	PhysicsSystem(World world) {
		super(Family.all(SdBodyComponent.class, TransformComponent.class).get())
		this.world = world
		this.bodiesQueue = new Array<Entity>()
	}

	@Override
	void update(float deltaTime) {
		super.update(deltaTime)
		float frameTime = Math.min(deltaTime, 0.25f)
		accumulator += frameTime
		if(accumulator >= MAX_STEP_TIME) {
			world.step(MAX_STEP_TIME, 6, 2)
			accumulator -= MAX_STEP_TIME

			//Entity Queue
			for (Entity entity : bodiesQueue) {
				TransformComponent tfm = tm.get(entity)
				SdBodyComponent bodyComp = bm.get(entity)
				Vector2 position = bodyComp.body.position
				tfm.position.x = position.x
				tfm.position.y = position.y

				VelocityComponent velCom = Mapper.velCom.get(entity)
				if(velCom) {
					bodyComp.body.linearVelocity = velCom.linearVelocity
					bodyComp.body.angularVelocity = velCom.angularVelocity

					if(velCom.applyVelocityToJointedBodies) {
						bodyComp.body.jointList.each {
							it.other.linearVelocity = velCom.linearVelocity
							it.other.angularVelocity = velCom.angularVelocity
						}
					}

					if(velCom.removeAfterProcessing) {
						entity.remove(VelocityComponent)
					}
				}

				if(bodyComp.isDead) {
					println('Removing a body and entity')
					world.destroyBody(bodyComp.body)
					engine.removeEntity(entity)
				}
			}
		}
		bodiesQueue.clear()
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		bodiesQueue.add(entity)
	}


}