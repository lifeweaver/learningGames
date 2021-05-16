package net.stardecimal.game.asteroids.entity.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import net.stardecimal.game.MyGames
import net.stardecimal.game.entity.components.BulletComponent
import net.stardecimal.game.entity.components.Mapper
import net.stardecimal.game.entity.components.SdBodyComponent

class BulletSystem extends IteratingSystem {
	MyGames parent

	@SuppressWarnings("unchecked")
	BulletSystem(MyGames parent) {
		super(Family.all(BulletComponent.class).get())
		this.parent = parent
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		//get box 2d body and bullet components
		SdBodyComponent b2body = Mapper.bCom.get(entity)
		BulletComponent bullet = Mapper.bulletCom.get(entity)


		//Bullets only last so long.
		if(bullet.maxLife >= 0) {
			bullet.maxLife -= deltaTime

		}

		if(bullet.maxLife < 0) {
			bullet.isDead = true
		}

		//check if bullet is dead
		if(bullet.isDead){
			b2body.isDead = true
		}
	}
}