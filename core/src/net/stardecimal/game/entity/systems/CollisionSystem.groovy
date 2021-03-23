package net.stardecimal.game.entity.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Fixture
import com.badlogic.gdx.physics.box2d.PolygonShape
import com.badlogic.gdx.physics.box2d.Shape
import net.stardecimal.game.LevelFactory
import net.stardecimal.game.PongGame
import net.stardecimal.game.entity.components.BulletComponent
import net.stardecimal.game.entity.components.CollisionComponent
import net.stardecimal.game.entity.components.EnemyComponent
import net.stardecimal.game.entity.components.Mapper
import net.stardecimal.game.entity.components.PlayerComponent
import net.stardecimal.game.entity.components.SdBodyComponent
import net.stardecimal.game.entity.components.TypeComponent
import net.stardecimal.game.loader.SdAssetManager

class CollisionSystem extends IteratingSystem {

	Sound bounce, paddleLeftLoss, paddleRightLoss
	final PongGame parent
	final LevelFactory levelFactory

	@SuppressWarnings('unchecked')
	CollisionSystem(PongGame pongGame, LevelFactory lvlFactory) {
		super(Family.all(CollisionComponent.class).get())
		parent = pongGame
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
		if (thisType.type == TypeComponent.PLAYER) {
			PlayerComponent pl = Mapper.playerCom.get(entity)
			if(collidedEntity) {
				TypeComponent type = Mapper.typeCom.get(collidedEntity)
				if (type) {
					switch (type.type) {
						case TypeComponent.SCENERY:
							println('player hit scenery')
							break
						case TypeComponent.BULLET:
							bounce.play()
							println('Player bounced ping pong!')
							BulletComponent bullet = Mapper.bulletCom.get(collidedEntity)
							SdBodyComponent bulletBodyCom = Mapper.bCom.get(collidedEntity)
							SdBodyComponent entityBodyCom = Mapper.bCom.get(entity)
							println("paddle x: ${entityBodyCom.body.position.x}, y: ${entityBodyCom.body.position.y}")
							println("bullet x: ${bulletBodyCom.body.position.x}, y: ${bulletBodyCom.body.position.y}")

							Vector2 newT = hitX(bullet.xVel, bullet.yVel)

							float change = 0
							if(bulletBodyCom.body.position.y < entityBodyCom.body.position.y) {
								change = -((entityBodyCom.body.position.y - bulletBodyCom.body.position.y) * 12 as float)
								println('going up: ' + change + ", newT.y: ${newT.y} result: ${newT.y + change as float}, newT.x: ${newT.x} result: ${newT.x + change as float}")
							} else if(bulletBodyCom.body.position.y > entityBodyCom.body.position.y){
								change = (bulletBodyCom.body.position.y - entityBodyCom.body.position.y) * 12 as float
								println('going up: ' + change + ", newT.y: ${newT.y} result: ${newT.y + change as float}, newT.x: ${newT.x} result: ${newT.x + change as float}")
							}

							bullet.xVel = newT.x + (newT.x == 0 ? change : 0) as float
							bullet.yVel = newT.y + (newT.y == 0 ? change : 0) as float
							break
					}
					cc.collisionEntity = null // collision handled reset component
				} else {
					println('type == null => I should check it out')
				}
			}
		} else if (thisType.type == TypeComponent.ENEMY) {
			if (collidedEntity) {
				TypeComponent type = Mapper.typeCom.get(collidedEntity)
				if (type != null) {
					switch (type.type) {
						case TypeComponent.SCENERY:
							println("enemy hit scenery")
							break
						case TypeComponent.BULLET:
							bounce.play()
							println('enemy bounced ping pong!')
							BulletComponent bullet = Mapper.bulletCom.get(collidedEntity)
							SdBodyComponent bulletBodyCom = Mapper.bCom.get(collidedEntity)
							SdBodyComponent entityBodyCom = Mapper.bCom.get(entity)

							if(!bulletBodyCom.isDead) {
								Vector2 newT = hitX(bullet.xVel, bullet.yVel)
								bullet.xVel = newT.x
								bullet.yVel = newT.y
							}
							break
						default:
							println("No matching type found")
					}
					cc.collisionEntity = null // collision handled reset component
				} else {
					println("type == null")
				}
			}
		} else if(thisType.type == TypeComponent.BULLET) {
			if(collidedEntity) {
				TypeComponent type = Mapper.typeCom.get(collidedEntity)
				if(type) {
					switch (type.type) {
//						case TypeComponent.SCENERY:
//							println('ping pong hi scenery')
//							break
						case TypeComponent.SCORE_WALL:
							BulletComponent bullet = Mapper.bulletCom.get(entity)
							bullet.isDead = true
							println('player scored')
							parent.playerScore += 1
							levelFactory.createPingPong()

							break
						default:
							println('ping pong hit type: ' + type.type)
							bounce.play()
							BulletComponent bullet = Mapper.bulletCom.get(entity)
							SdBodyComponent bulletBodyCom = Mapper.bCom.get(entity)
							SdBodyComponent collidedBodyCom = Mapper.bCom.get(collidedEntity)

							Vector2 newT = hitY(bullet.xVel, bullet.yVel)
							bullet.xVel = newT.x
							bullet.yVel = newT.y

							break
					}
					cc.collisionEntity = null // collision handled reset component
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