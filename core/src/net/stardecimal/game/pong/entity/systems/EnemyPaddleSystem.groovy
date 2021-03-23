package net.stardecimal.game.pong.entity.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.MathUtils
import net.stardecimal.game.pong.LevelFactory
import net.stardecimal.game.entity.components.EnemyComponent
import net.stardecimal.game.entity.components.Mapper
import net.stardecimal.game.entity.components.SdBodyComponent

class EnemyPaddleSystem extends IteratingSystem{

	private LevelFactory levelFactory

	@SuppressWarnings("unchecked")
	EnemyPaddleSystem(LevelFactory lvlf) {
		super(Family.all(EnemyComponent.class).get())
		levelFactory = lvlf
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		EnemyComponent enemyCom = Mapper.enemyCom.get(entity)

		if(enemyCom) {
			SdBodyComponent bEnemy = Mapper.bCom.get(entity)
			SdBodyComponent bPingPong = Mapper.bCom.get(levelFactory.pingPong)
//			float distance = bEnemy.body.position.dst(bPingPong.body.position)

			if(bEnemy.body.position.y > bPingPong.body.position.y + 1) {
				bEnemy.body.setLinearVelocity(bEnemy.body.linearVelocity.x, MathUtils.lerp(bEnemy.body.linearVelocity.y, -10f, 0.2f))
			} else if(bEnemy.body.position.y < bPingPong.body.position.y - 1) {
				bEnemy.body.setLinearVelocity(bEnemy.body.linearVelocity.x, MathUtils.lerp(bEnemy.body.linearVelocity.y, 10f, 0.2f))
			} else {
				bEnemy.body.setLinearVelocity(bEnemy.body.linearVelocity.x, MathUtils.lerp(bEnemy.body.linearVelocity.y, 0, 0.1f))
			}

		}
	}
}