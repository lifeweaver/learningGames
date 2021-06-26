package com.stardecimal.game.entity.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile
import com.badlogic.gdx.math.Vector2
import com.stardecimal.game.GameJamGame
import com.stardecimal.game.LevelFactory
import com.stardecimal.game.entity.components.CollisionComponent
import com.stardecimal.game.entity.components.SdBodyComponent
import com.stardecimal.game.entity.components.TypeComponent
import com.stardecimal.game.entity.util.Mapper

class CollisionSystem extends IteratingSystem {
	final LevelFactory levelFactory
	final GameJamGame parent

	@SuppressWarnings('unchecked')
	CollisionSystem(GameJamGame game, LevelFactory lvlFactory) {
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
				if(collidedType == TypeComponent.TYPES.BULLET) {
					int worth = Mapper.scoreCom.get(collidedEntity).worth
					collidedBody.isDead = true
					levelFactory.playerScore = levelFactory.playerScore - worth
				}
				//TODO: do something
				cc.collisionEntity = null
			}
		}
		cc.collisionEntity = null
	}
}
