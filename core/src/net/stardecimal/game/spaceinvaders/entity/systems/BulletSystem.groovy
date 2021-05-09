package net.stardecimal.game.spaceinvaders.entity.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import net.stardecimal.game.MyGames
import net.stardecimal.game.spaceinvaders.LevelFactory
import net.stardecimal.game.entity.components.BulletComponent
import net.stardecimal.game.entity.components.Mapper
import net.stardecimal.game.entity.components.SdBodyComponent

class BulletSystem extends IteratingSystem {
	LevelFactory levelFactory
	MyGames parent

	@SuppressWarnings("unchecked")
	BulletSystem(MyGames parent, LevelFactory lvlFactory) {
		super(Family.all(BulletComponent.class).get())
		this.levelFactory = lvlFactory
		this.parent = parent
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		//get box 2d body and bullet components
		SdBodyComponent b2body = Mapper.bCom.get(entity)
		BulletComponent bullet = Mapper.bulletCom.get(entity)

		// apply bullet velocity to bullet body
		b2body.body.setLinearVelocity(bullet.xVel, bullet.yVel)

		//check if bullet is dead
		if(bullet.isDead){
			b2body.isDead = true
		}
	}
}
