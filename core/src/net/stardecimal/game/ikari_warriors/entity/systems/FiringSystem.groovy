package net.stardecimal.game.ikari_warriors.entity.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import net.stardecimal.game.entity.components.BulletComponent
import net.stardecimal.game.entity.components.Mapper
import net.stardecimal.game.entity.components.SdBodyComponent
import net.stardecimal.game.entity.components.TypeComponent
import net.stardecimal.game.ikari_warriors.LevelFactory

class FiringSystem extends IteratingSystem {
	LevelFactory levelFactory

	@SuppressWarnings("unchecked")
	FiringSystem(LevelFactory lvlFactory) {
		super(Family.all(BulletComponent.class).get())
		this.levelFactory = lvlFactory
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		//get box 2d body and bullet components
		SdBodyComponent b2body = Mapper.bCom.get(entity)
		BulletComponent bullet = Mapper.bulletCom.get(entity)

		// apply bullet velocity to bullet body
		b2body.body.setLinearVelocity(bullet.xVel, bullet.yVel)

		//Bullets only last so long.
		if(bullet.maxLife >= 0) {
			bullet.maxLife -= deltaTime
		}

		if(bullet.maxLife < 0) {
			bullet.isDead = true
			if(Mapper.typeCom.get(entity).type == TypeComponent.TYPES.GRENADE) {
				levelFactory.createBoom(b2body.body.position)
			}
		}

		//check if bullet is dead
		if(bullet.isDead){
			b2body.isDead = true
		}
	}
}