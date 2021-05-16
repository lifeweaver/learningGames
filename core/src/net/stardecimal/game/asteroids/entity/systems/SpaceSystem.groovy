package net.stardecimal.game.asteroids.entity.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Array
import net.stardecimal.game.entity.components.Mapper
import net.stardecimal.game.entity.components.SdBodyComponent
import net.stardecimal.game.entity.systems.RenderingSystem

class SpaceSystem extends IteratingSystem {
	private Array<Entity> spaceQueue

	@SuppressWarnings('uncheck')
	SpaceSystem() {
		super(Family.all(SdBodyComponent.class).get())
		spaceQueue = new Array<Entity>()
	}

	@Override
	void update(float deltaTime) {
		super.update(deltaTime)
		Vector2 screenSize = RenderingSystem.getScreenSizeInMeters()
		float maxX = screenSize.x / RenderingSystem.PPM
		float maxY = screenSize.y / RenderingSystem.PPM

		spaceQueue.each {
			SdBodyComponent bCom = Mapper.bCom.get(it)

			if(bCom.body.position.x < 0) {
				bCom.body.setTransform(maxX, bCom.body.position.y, bCom.body.angle)
			} else if(bCom.body.position.x > maxX) {
				bCom.body.setTransform(0, bCom.body.position.y, bCom.body.angle)
			} else if(bCom.body.position.y < 0) {
				bCom.body.setTransform(bCom.body.position.x, maxY, bCom.body.angle)
			} else if(bCom.body.position.y > maxY) {
				bCom.body.setTransform(bCom.body.position.x, 0, bCom.body.angle)
			}
		}
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		spaceQueue.add(entity)
	}
}
