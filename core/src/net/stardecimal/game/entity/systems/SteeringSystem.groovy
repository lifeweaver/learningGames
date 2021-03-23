package net.stardecimal.game.entity.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.ai.GdxAI
import net.stardecimal.game.entity.components.Mapper
import net.stardecimal.game.entity.components.SteeringComponent
import net.stardecimal.game.entity.components.TypeComponent

class SteeringSystem extends IteratingSystem {

	@SuppressWarnings("unchecked")
	SteeringSystem() {
		super(Family.all(SteeringComponent.class).get())
	}


	@Override
	void update(float deltaTime) {
		super.update(deltaTime)
		GdxAI.timepiece.update(deltaTime)
	}


	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		SteeringComponent steer = Mapper.sCom.get(entity)
		if(!Mapper.bCom.get(entity).isDead && steer.position.x) {

			steer.update(deltaTime)
		}
	}
}