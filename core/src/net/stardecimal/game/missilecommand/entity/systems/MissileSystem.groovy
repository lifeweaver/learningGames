package net.stardecimal.game.missilecommand.entity.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.Vector2
import net.stardecimal.game.MyGames
import net.stardecimal.game.entity.components.BulletComponent
import net.stardecimal.game.entity.components.Mapper
import net.stardecimal.game.entity.components.ParticleEffectComponent
import net.stardecimal.game.entity.components.SdBodyComponent
import net.stardecimal.game.entity.systems.RenderingSystem
import net.stardecimal.game.missilecommand.LevelFactory

class MissileSystem extends IteratingSystem {
	private LevelFactory levelFactory
	private MyGames parent

	@SuppressWarnings("unchecked")
	MissileSystem(MyGames parent, LevelFactory lvlFactory){
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

		//get bullet pos
		float bx = b2body.body.position.x
		float by = b2body.body.position.y

		//Update distance moved
		if(bullet.startPos) {
			bullet.distMoved = bullet.startPos.dst(b2body.body.position)
		}

		Vector2 screenSize = RenderingSystem.getScreenSizeInMeters()
		if(bx < 0 || bx > screenSize.x / RenderingSystem.PPM || by < 0 || by > screenSize.y / RenderingSystem.PPM){
			bullet.isDead = true
		}

		if(bullet.maxDist && bullet.distMoved >= bullet.maxDist) {
			bullet.xVel = 0
			bullet.yVel = 0
			bullet.isDead = true
			levelFactory.createBoom(new Vector2(b2body.body.position.x, b2body.body.position.y))
		}

//		check if bullet is dead
		if(bullet.isDead) {
			ParticleEffectComponent peCom = Mapper.peCom.get(bullet.particleEffect)
			if(peCom) {
				peCom.isDead = true
			}

			b2body.isDead = true
		}
	}
}
