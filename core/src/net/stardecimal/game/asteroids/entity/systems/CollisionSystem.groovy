package net.stardecimal.game.asteroids.entity.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.OrthographicCamera
import net.stardecimal.game.MyGames
import net.stardecimal.game.asteroids.LevelFactory
import net.stardecimal.game.entity.components.CollisionComponent
import net.stardecimal.game.entity.components.Mapper
import net.stardecimal.game.entity.components.SdBodyComponent
import net.stardecimal.game.entity.components.TypeComponent

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
					case TypeComponent.TYPES.BULLET:
						collidedBody.isDead = true
						body.isDead = true
						break

					case TypeComponent.TYPES.ENEMY:
						int worth = Mapper.scoreCom.get(collidedEntity).worth
						collidedBody.isDead = true
						body.isDead = true
						levelFactory.enemyBlownUp.play(0.2)
						levelFactory.playerScore = levelFactory.playerScore + worth
						break

					case TypeComponent.TYPES.PLAYER:
						//TODO make it so only shots fired by the enemy hurt the player, collision masks?
						OrthographicCamera cam = Mapper.playerCom.get(collidedEntity).cam
						collidedBody.isDead = true
						body.isDead = true
						levelFactory.playerBlownUp.play()
						levelFactory.playerLives = levelFactory.playerLives - 1
						if(levelFactory.playerLives >= 0) {
							levelFactory.createPlayer(cam)
						}
						break
				}
				cc.collisionEntity = null
			}
		} else if(type == TypeComponent.TYPES.ENEMY) {
			if (collidedEntity) {
				SdBodyComponent collidedBody = Mapper.bCom.get(collidedEntity)
				SdBodyComponent body = Mapper.bCom.get(entity)
				switch (collidedType) {
					default:
						println("Enemy Collided with: ${collidedType}")
				}
				cc.collisionEntity = null
			}
		}
	}

}
