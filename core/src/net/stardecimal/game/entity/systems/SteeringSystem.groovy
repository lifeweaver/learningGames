package net.stardecimal.game.entity.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.ai.GdxAI
import net.stardecimal.game.entity.components.Mapper
import net.stardecimal.game.entity.components.SteeringComponent
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class SteeringSystem extends IteratingSystem {
	Logger log

	@SuppressWarnings("unchecked")
	SteeringSystem() {
		super(Family.all(SteeringComponent.class).get())
		log = LoggerFactory.getLogger(SteeringSystem)
	}


	@Override
	void update(float deltaTime) {
		super.update(deltaTime)
		GdxAI.timepiece.update(deltaTime)
	}


	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		SteeringComponent sCom = Mapper.sCom.get(entity)
		if(!Mapper.bCom.get(entity).isDead && sCom.steeringBehavior) {
			sCom.update(deltaTime)
		}
	}
}