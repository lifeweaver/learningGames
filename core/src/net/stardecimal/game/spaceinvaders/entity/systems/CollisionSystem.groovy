package net.stardecimal.game.spaceinvaders.entity.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import net.stardecimal.game.MyGames
import net.stardecimal.game.entity.components.CollisionComponent
import net.stardecimal.game.entity.components.Mapper
import net.stardecimal.game.entity.components.SdBodyComponent
import net.stardecimal.game.entity.components.TypeComponent
import net.stardecimal.game.spaceinvaders.LevelFactory

class CollisionSystem extends IteratingSystem {
	final LevelFactory levelFactory
	final MyGames parent


	@SuppressWarnings('unchecked')
	CollisionSystem(MyGames game, LevelFactory lvlFactory) {
		super(Family.all(CollisionComponent.class).get())
		parent = game
		levelFactory = lvlFactory
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		CollisionComponent cc = Mapper.collisionCom.get(entity)
		int type = Mapper.typeCom.get(entity).type

		// collided entity
		Entity collidedEntity = cc.collisionEntity
		int collidedType = 10000
		if (collidedEntity) {
			TypeComponent thisType = Mapper.typeCom.get(collidedEntity)
			collidedType = thisType ? thisType.type : collidedType
		}

		// collisions
		if (type == TypeComponent.TYPES.BULLET) {
			if (collidedEntity) {
				SdBodyComponent collidedBody = Mapper.bCom.get(collidedEntity)
				SdBodyComponent body = Mapper.bCom.get(entity)

				switch (collidedType) {
					case TypeComponent.TYPES.SCENERY:
						body.isDead = true
						break

					case TypeComponent.TYPES.BULLET:
						collidedBody.isDead = true
						body.isDead = true
						break

					case TypeComponent.TYPES.ENEMY:
						collidedBody.isDead = true
						body.isDead = true
						levelFactory.enemyBlownUp.play()
						break

					case TypeComponent.TYPES.PLAYER:
						collidedBody.isDead = true
						body.isDead = true
						levelFactory.playerBlownUp.play()
						break

					case TypeComponent.TYPES.DESTRUCTIBLE_SCENERY:
						collidedBody.isDead = true
						body.isDead = true
						break
				}
			}
		}
	}

}
