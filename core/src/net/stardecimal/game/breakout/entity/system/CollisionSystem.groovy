package net.stardecimal.game.breakout.entity.system

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.math.Vector2
import net.stardecimal.game.MyGames
import net.stardecimal.game.breakout.entity.components.PowerUpComponent
import net.stardecimal.game.entity.components.BulletComponent
import net.stardecimal.game.entity.components.CollisionComponent
import net.stardecimal.game.entity.components.Mapper
import net.stardecimal.game.entity.components.SdBodyComponent
import net.stardecimal.game.entity.components.TextureComponent
import net.stardecimal.game.entity.components.TypeComponent
import net.stardecimal.game.entity.systems.RenderingSystem
import net.stardecimal.game.loader.SdAssetManager
import net.stardecimal.game.breakout.LevelFactory
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class CollisionSystem extends IteratingSystem {

	Sound bounce
	final MyGames parent
	final LevelFactory levelFactory
	float rightWallBoundary = RenderingSystem.getScreenSizeInMeters().x / RenderingSystem.PPM as float
	private static final Logger log = LoggerFactory.getLogger(CollisionSystem)

	@SuppressWarnings('unchecked')
	CollisionSystem(MyGames game, LevelFactory lvlFactory) {
		super(Family.all(CollisionComponent.class).get())
		parent = game
		levelFactory = lvlFactory
		bounce = parent.assetManager.manager.get(SdAssetManager.bounce)
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		CollisionComponent cc = Mapper.collisionCom.get(entity)
		int type = Mapper.typeCom.get(entity).type

		// collided entity
		Entity collidedEntity = cc.collisionEntity
		int collidedType = 10000
		if(collidedEntity) {
			TypeComponent thisType = Mapper.typeCom.get(collidedEntity)
			collidedType = thisType ? thisType.type : collidedType
		}

		// do player collisions
		if (type == TypeComponent.PLAYER) {
			if (collidedEntity) {
				switch (collidedType) {
					case TypeComponent.BULLET:
						bounce.play()
						BulletComponent bullet = Mapper.bulletCom.get(collidedEntity)
						SdBodyComponent bulletBodyCom = Mapper.bCom.get(collidedEntity)
						SdBodyComponent entityBodyCom = Mapper.bCom.get(entity)

						Vector2 newT = hitY(bullet.xVel, bullet.yVel)

						float change = 0
						if (bulletBodyCom.body.position.x < entityBodyCom.body.position.x) {
							change = -((entityBodyCom.body.position.x - bulletBodyCom.body.position.x) * 12 as float)
//								println('going up: ' + change + ", newT.x: ${newT.x} result: ${newT.x + change as float}, newT.y: ${newT.y}")
						} else if (bulletBodyCom.body.position.x > entityBodyCom.body.position.x) {
							change = (bulletBodyCom.body.position.x - entityBodyCom.body.position.x) * 12 as float
//								println('going up: ' + change + ", newT.x: ${newT.x} result: ${newT.x + change as float}, newT.y: ${newT.y}")
						}

						bullet.xVel = newT.x + (newT.x == 0 ? change : 0) as float
						bullet.yVel = newT.y + (newT.y == 0 ? change : 0) as float
						break
				}
				cc.collisionEntity = null // collision handled reset component
			}
		} else if (type == TypeComponent.BULLET) {
			if (collidedEntity) {
				BulletComponent bullet = Mapper.bulletCom.get(entity)
				SdBodyComponent collidedBodyCom = Mapper.bCom.get(collidedEntity)
				Vector2 collidedBodyPosition = collidedBodyCom?.body?.position
				PowerUpComponent powerUp = ComponentMapper.getFor(PowerUpComponent.class).get(entity)

				switch (collidedType) {
					case TypeComponent.POWER_UP:
						powerUp.noBounceCount = 10
						collidedBodyCom.isDead = true
						boxBounce(bullet, powerUp)
						break

					case TypeComponent.ENEMY:
						collidedBodyCom.isDead = true
						boxBounce(bullet, powerUp)
						break

					case TypeComponent.ENEMY_DOUBLE:
						TypeComponent thisType = Mapper.typeCom.get(collidedEntity)
						thisType.type = TypeComponent.ENEMY
						TextureComponent texCom = Mapper.texCom.get(collidedEntity)
						texCom.region = levelFactory.defaultBoxTex
						boxBounce(bullet, powerUp)
						break

					case TypeComponent.ENEMY_EXPLODE:
						//Move logic to function so it handles an explosion exploding another explode block, if you want to get that fancy
						def blockTypes = [TypeComponent.ENEMY, TypeComponent.ENEMY_DOUBLE, TypeComponent.ENEMY_EXPLODE, TypeComponent.POWER_UP]
						List<Entity> blocks = engine.entities.findAll {Entity thisEntity ->
							blockTypes.contains(Mapper.typeCom.get(thisEntity).type)
						}

						if(!collidedBodyPosition) {
							break
						}

						//boxes on each side
						float rightX = collidedBodyPosition.x + (collidedBodyCom.width / 2) as float
						float leftX = collidedBodyPosition.x - (collidedBodyCom.width / 2) as float

						//boxes above and below
						float topY = collidedBodyPosition.y + (collidedBodyCom.height / 2) as float
						float bottomY = collidedBodyPosition.y - (collidedBodyCom.height / 2) as float

//							log.info("rightX: ${rightX}, leftX: ${leftX}, topY: ${topY}, bottomY: ${bottomY}")
//							log.info("blocks found: ${blocks.size()}")

						blocks.each { Entity thisEntity ->
							SdBodyComponent bodyCom = Mapper.bCom.get(thisEntity)
							[rightX, leftX].each {float x ->
								[topY, bottomY].each {float y ->
									Vector2 newVector = new Vector2(x, y)
									if(positionInside(bodyCom, newVector)) {
										bodyCom.isDead = true
									}
								}
							}
						}

						collidedBodyCom.isDead = true
						boxBounce(bullet, powerUp)
						break

					default:
						if(!collidedBodyPosition) {
							break
						}

						println('ping pong hit type: ' + collidedType)
						bounce.play()
						Vector2 newT = null
						if(collidedBodyPosition.x == 0 || collidedBodyPosition.x == rightWallBoundary) {
							newT = hitX(bullet.xVel, bullet.yVel)
						} else {
							newT = hitY(bullet.xVel, bullet.yVel)
						}

						bullet.xVel = newT.x
						bullet.yVel = newT.y

						break
				}
				cc.collisionEntity = null // collision handled reset component
			}
		} else {
			println("Unhandled type: ${type}")
		}

	}

	//Calculate the bodyArea if it hasn't already been, I'll modify this if I ever want to add more than one fixture.
	// Maybe https://gamedev.stackexchange.com/a/184878/149628
	static Vector2[] getBodyArea(SdBodyComponent sdBody) {
		Vector2 corner1 = new Vector2(sdBody.body.position.x - sdBody.width as float, sdBody.body.position.y - sdBody.height as float)
		Vector2 corner2 = new Vector2(sdBody.body.position.x + sdBody.width as float, sdBody.body.position.y + sdBody.height as float)
		return [corner1, corner2]
	}

	static boolean positionInside(SdBodyComponent sdBody, Vector2 targetPosition) {
		Vector2[] bodyAreas = getBodyArea(sdBody)
		Vector2 corner1 = bodyAreas.first()
		Vector2 corner2 = bodyAreas.last()

		if(targetPosition.x >= corner1.x && targetPosition.x <= corner2.x && targetPosition.y >= corner1.y && targetPosition.y <= corner2.y) {
			return true
		}

		return false
	}

	void boxBounce(BulletComponent bullet, PowerUpComponent powerUp) {
		parent.playerScore += 1

		if(powerUp.noBounceCount < 1) {
			Vector2 newT = hitY(bullet.xVel, bullet.yVel)
			bullet.xVel = newT.x
			bullet.yVel = newT.y
		} else {
			powerUp.noBounceCount = powerUp.noBounceCount - 1
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