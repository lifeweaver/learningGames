package net.stardecimal.game.asteroids.entity.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Vector2
import net.stardecimal.game.MyGames
import net.stardecimal.game.asteroids.LevelFactory
import net.stardecimal.game.entity.components.CollisionComponent
import net.stardecimal.game.entity.components.Mapper
import net.stardecimal.game.entity.components.ScoreComponent
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
						destroyAndAddWorth(body, collidedBody, collidedEntity)
						break

					case TypeComponent.TYPES.PLAYER:
						OrthographicCamera cam = Mapper.playerCom.get(collidedEntity).cam
						collidedBody.isDead = true
						body.isDead = true
						levelFactory.playerBlownUp.play()
						levelFactory.playerLives = levelFactory.playerLives - 1
						if(levelFactory.playerLives >= 0) {
							levelFactory.createPlayer(cam)
						}
						break

					case TypeComponent.TYPES.ASTEROID:
						destroyAndAddWorth(body, collidedBody, collidedEntity)

						levelFactory.createAsteroid(collidedBody.body.position, new Vector2(3, 3), TypeComponent.TYPES.MEDIUM_ASTEROID)
						levelFactory.createAsteroid(collidedBody.body.position, new Vector2(-3, -3), TypeComponent.TYPES.MEDIUM_ASTEROID)
						break

					case TypeComponent.TYPES.MEDIUM_ASTEROID:
						destroyAndAddWorth(body, collidedBody, collidedEntity)

						levelFactory.createAsteroid(collidedBody.body.position, new Vector2(3, 3), TypeComponent.TYPES.MINI_ASTEROID)
						levelFactory.createAsteroid(collidedBody.body.position, new Vector2(-3, -3), TypeComponent.TYPES.MINI_ASTEROID)
						break

					case TypeComponent.TYPES.MINI_ASTEROID:
						destroyAndAddWorth(body, collidedBody, collidedEntity)
						break
				}
			}
			cc.collisionEntity = null
		} else if(type == TypeComponent.TYPES.ASTEROID || type == TypeComponent.TYPES.MEDIUM_ASTEROID || type == TypeComponent.TYPES.MINI_ASTEROID) {
			if (collidedEntity) {
				SdBodyComponent collidedBody = Mapper.bCom.get(collidedEntity)
				SdBodyComponent body = Mapper.bCom.get(entity)
				switch (collidedType) {
					case TypeComponent.TYPES.PLAYER:
						OrthographicCamera cam = Mapper.playerCom.get(collidedEntity).cam
						collidedBody.isDead = true
						levelFactory.playerLives -= 1
						if(levelFactory.playerLives >= 0) {
							levelFactory.createPlayer(cam)
						}
						break

					case TypeComponent.TYPES.ENEMY:
						collidedBody.isDead = true
						break
				}
				cc.collisionEntity = null
			}
		}
	}

	void destroyAndAddWorth(SdBodyComponent body, SdBodyComponent collidedBody, Entity collidedEntity ) {
		if(!collidedBody.isDead) {
			ScoreComponent scoreCom = Mapper.scoreCom.get(collidedEntity)
			int worth = scoreCom ? scoreCom.worth : 50
			collidedBody.isDead = true
			body.isDead = true
			levelFactory.enemyBlownUp.play(0.2)
			levelFactory.playerScore = levelFactory.playerScore + worth
		}
	}
}
