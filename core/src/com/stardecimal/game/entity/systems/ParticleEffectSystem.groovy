package com.stardecimal.game.entity.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.utils.Array
import com.stardecimal.game.entity.util.Mapper
import com.stardecimal.game.entity.components.ParticleEffectComponent
import com.stardecimal.game.entity.components.SdBodyComponent

class ParticleEffectSystem extends IteratingSystem {
	private static final boolean shouldRender = true

	private Array<Entity> renderQueue
	private SpriteBatch batch
	private OrthographicCamera camera
	private World world

	@SuppressWarnings("unchecked")
	ParticleEffectSystem(SpriteBatch sb, OrthographicCamera cam, World wd) {
		super(Family.all(ParticleEffectComponent.class).get())
		priority = 10
		renderQueue = new Array<Entity>()
		batch = sb
		camera = cam
		world = wd
	}

	@Override
	void update(float deltaTime) {
		super.update(deltaTime)
		batch.setProjectionMatrix(camera.combined)
//		batch.enableBlending()
		// Render PE
		if(shouldRender) {
			batch.begin()
			for (Entity entity : renderQueue) {
				ParticleEffectComponent pec = Mapper.peCom.get(entity)
				if(pec.destroyExistingParticles) {
					pec.particleEffect.emitters.each {
						it.reset()
					}
				} else {
					if(pec.isAttached && pec.angleEmitters) {
						adjustParticleAngles(pec)
					}

					pec.particleEffect.draw(batch, deltaTime)
				}
			}
			batch.end()
		}
		renderQueue.clear()
	}

	static void adjustParticleAngles(ParticleEffectComponent pec) {
		float angle = (pec.attachedBody.angle * MathUtils.radiansToDegrees + 270) % 360 as float

		pec.particleEffect.emitters.each {
			it.angle.setLow(angle)
			it.angle.setHigh(angle)
		}
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		ParticleEffectComponent pec = Mapper.peCom.get(entity)
		if(pec.isDead) {
			pec.timeTilDeath -= deltaTime
		}

		// Move PE if attached
		if(pec.isAttached) {
			pec.particleEffect.setPosition(
					pec.attachedBody.getPosition().x + pec.xOffset as float,
					pec.attachedBody.getPosition().y + pec.yOffset as float)
		}

		// free PE if completed
		if(pec.particleEffect.isComplete() || pec.timeTilDeath <= 0 || (pec.attachedBody && Mapper.bCom.get(pec.attachedBody.userData as Entity).isDead && pec.killOnParentBodyDeath)) {
			SdBodyComponent sdBody = Mapper.bCom.get(entity)

			//If there is a body, destroy it before removing the entity
			if(sdBody) {
				world.destroyBody(sdBody.body)
			}
			getEngine().removeEntity(entity)
		}else{
			renderQueue.add(entity)
		}
	}
}
