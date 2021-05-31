package net.stardecimal.game.pacman.entity.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.Vector2
import net.stardecimal.game.MyGames
import net.stardecimal.game.entity.components.CollisionComponent
import net.stardecimal.game.entity.components.Mapper
import net.stardecimal.game.entity.components.SdBodyComponent
import net.stardecimal.game.entity.components.TypeComponent
import net.stardecimal.game.pacman.LevelFactory

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
		if (type == TypeComponent.TYPES.PLAYER) {
			if (collidedEntity) {
				SdBodyComponent collidedBody = Mapper.bCom.get(collidedEntity)
				SdBodyComponent body = Mapper.bCom.get(entity)

				switch (collidedType) {
					case TypeComponent.TYPES.OTHER:
						int worth = Mapper.scoreCom.get(collidedEntity).worth
						collidedBody.isDead = true
						//TODO: play sound
						levelFactory.playerScore = levelFactory.playerScore + worth
						break

					case TypeComponent.TYPES.ENEMY:
						//TODO: check if player just ate powerup
						int worth = Mapper.scoreCom.get(collidedEntity).worth
						collidedBody.isDead = true
						body.isDead = true
						levelFactory.playerScore = levelFactory.playerScore + worth
						break
				}
				cc.collisionEntity = null
			} else {
				//Handle the tile collisions for the player
				SdBodyComponent body = Mapper.bCom.get(entity)
				Vector2 pos = body.body.position
				boolean collisionX = false
				boolean collisionY = false
				float playerHeight = 0.5
				float playerWidth = 0.5

				boolean isGoingLeft = body.body.linearVelocity.x < 0
				boolean isGoingRight = body.body.linearVelocity.x > 0
				boolean isGoingDown = body.body.linearVelocity.y < 0
				boolean isGoingUp = body.body.linearVelocity.y > 0

				if(isGoingLeft) {
					collisionY = levelFactory.isCellBlocked(pos.x - playerWidth as float, pos.y)
				} else if(isGoingRight) {
					collisionY = levelFactory.isCellBlocked(pos.x + playerWidth as float, pos.y)
				}

				if(isGoingDown) {
					collisionY = levelFactory.isCellBlocked(pos.x, pos.y - playerHeight as float)
				} else if(isGoingUp) {
					collisionY = levelFactory.isCellBlocked(pos.x, pos.y + playerHeight as float)
				}

				if(collisionX || (collisionY && body.body.linearVelocity.x)) {
					//Reverse the velocity
					float xVel = body.body.linearVelocity.x * -1 as float
					body.body.setLinearVelocity(0, 0)

					while(levelFactory.isCellBlocked(body.body.position.x, body.body.position.y)) {
						float newX = body.body.position.x + (xVel * deltaTime) as float
						body.body.setTransform(newX, body.body.position.y, 0)
					}
				}

				if(collisionY || (collisionX && body.body.linearVelocity.y)) {
					//Reverse the velocity
					float yVel = body.body.linearVelocity.y * -1 as float
					body.body.setLinearVelocity(0, 0)

					while(levelFactory.isCellBlocked(body.body.position.x, body.body.position.y)) {
						float newY = body.body.position.y + (yVel * deltaTime) as float
						body.body.setTransform(body.body.position.x, newY, 0)
					}
				}
			}
		}
	}
}