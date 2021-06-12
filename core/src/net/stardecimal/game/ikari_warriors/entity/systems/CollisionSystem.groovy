package net.stardecimal.game.ikari_warriors.entity.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Vector2
import net.stardecimal.game.entity.components.BulletComponent
import net.stardecimal.game.entity.components.CollisionComponent
import net.stardecimal.game.entity.components.Mapper
import net.stardecimal.game.entity.components.SdBodyComponent
import net.stardecimal.game.entity.components.TypeComponent
import net.stardecimal.game.ikari_warriors.LevelFactory

class CollisionSystem extends IteratingSystem {
	LevelFactory levelFactory

	@SuppressWarnings('unchecked')
	CollisionSystem(LevelFactory lvlFactory) {
		super(Family.all(CollisionComponent.class).get())
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
				BulletComponent bulletCom = Mapper.bulletCom.get(entity)
				switch (collidedType) {
					case TypeComponent.TYPES.BULLET:
						//ignore
						break

					case TypeComponent.TYPES.GUN_SOLDIER:
						if(bulletCom.owner != BulletComponent.Owner.ENEMY) {
							int worth = Mapper.scoreCom.get(collidedEntity).worth
							collidedBody.isDead = true
							body.isDead = true
							levelFactory.playerScore = levelFactory.playerScore + worth
						}
						break

					case TypeComponent.TYPES.PLAYER:
						if(collidedBody.invulnerabilityTime > 0) {
							body.isDead = true
							break
						}

						if(bulletCom.owner != BulletComponent.Owner.PLAYER) {
							OrthographicCamera cam = Mapper.playerCom.get(collidedEntity).cam
							Vector2 startPos = collidedBody.body.position
							collidedBody.isDead = true
							body.isDead = true
							levelFactory.playerLives = levelFactory.playerLives - 1
							if(levelFactory.playerLives >= 0) {
								levelFactory.createPlayer(cam, startPos)
							}
						}
						break
				}
				cc.collisionEntity = null
			}
		}
	}
}
