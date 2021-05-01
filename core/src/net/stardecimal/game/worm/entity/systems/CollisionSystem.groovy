package net.stardecimal.game.worm.entity.systems

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.math.Vector2
import net.stardecimal.game.MyGames
import net.stardecimal.game.entity.components.CollisionComponent
import net.stardecimal.game.entity.components.Mapper
import net.stardecimal.game.entity.components.SdBodyComponent
import net.stardecimal.game.entity.components.TypeComponent
import net.stardecimal.game.loader.SdAssetManager
import net.stardecimal.game.worm.LevelFactory
import net.stardecimal.game.worm.entity.components.PlayerComponent

class CollisionSystem extends IteratingSystem {

	Sound bounce, paddleLeftLoss, paddleRightLoss
	final MyGames parent
	final LevelFactory levelFactory

	@SuppressWarnings('unchecked')
	CollisionSystem(MyGames game, LevelFactory lvlFactory) {
		super(Family.all(CollisionComponent.class).get())
		parent = game
		levelFactory = lvlFactory
		bounce = parent.assetManager.manager.get(SdAssetManager.bounce)
		paddleLeftLoss = parent.assetManager.manager.get(SdAssetManager.paddleLeftLoss)
		paddleRightLoss = parent.assetManager.manager.get(SdAssetManager.paddleRightLoss)
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		CollisionComponent cc = Mapper.collisionCom.get(entity)
		TypeComponent thisType = Mapper.typeCom.get(entity)

		// collided entity
		Entity collidedEntity = cc.collisionEntity

		// do player collisions
		if (thisType.type == TypeComponent.TYPES.PLAYER) {
			SdBodyComponent body = Mapper.bCom.get(entity)
			if(collidedEntity) {
				TypeComponent type = Mapper.typeCom.get(collidedEntity)
				if (type) {
					switch (type.type) {
						case TypeComponent.TYPES.SCENERY:
							println('player hit scenery')
							//TODO: end game
							break
						case TypeComponent.TYPES.SCORE_WALL:
							//TODO: different sound
							bounce.play()
							println('Player ate fruit!')
							Mapper.bCom.get(collidedEntity).isDead = true
							parent.playerScore += 1
							levelFactory.createFruit()
							PlayerComponent playerComponent = ComponentMapper.getFor(PlayerComponent.class).get(entity)
							playerComponent.length = playerComponent.length + 1
							break
					}
					cc.collisionEntity = null // collision handled reset component
				} else {
					println('type == null => I should check it out')
				}
			}
		} else {
			println("Unhandled type: ${thisType.type}")
		}

	}

	Vector2 hitX(float x, float y) {
		return hitX(new Vector2(x, y))
	}

	Vector2 hitX(Vector2 v) {
		v.x *= -1
		return v
	}

	Vector2 hitY(float x, float y) {
		return hitY(new Vector2(x, y))
	}

	Vector2 hitY(Vector2 v) {
		v.y *= -1
		return v
	}
}