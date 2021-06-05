package net.stardecimal.game.pacman.entity.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile
import com.badlogic.gdx.math.Vector2
import net.stardecimal.game.DFUtils
import net.stardecimal.game.MyGames
import net.stardecimal.game.entity.components.CollisionComponent
import net.stardecimal.game.entity.components.Mapper
import net.stardecimal.game.entity.components.SdBodyComponent
import net.stardecimal.game.entity.components.TypeComponent
import net.stardecimal.game.entity.systems.RenderingSystem
import net.stardecimal.game.pacman.LevelFactory
import net.stardecimal.game.pacman.entity.component.PowerUpComponent

class CollisionSystem extends IteratingSystem {
	final LevelFactory levelFactory
	final MyGames parent
	float tileHeight
	float tileWidth
	def offsetX
	StaticTiledMapTile blankTile

	@SuppressWarnings('unchecked')
	CollisionSystem(MyGames game, LevelFactory lvlFactory) {
		super(Family.all(CollisionComponent.class).get())
		parent = game
		levelFactory = lvlFactory
		tileHeight = levelFactory.collisionLayer.tileHeight * RenderingSystem.PIXELS_TO_METRES as float
		tileWidth = levelFactory.collisionLayer.tileWidth * RenderingSystem.PIXELS_TO_METRES as float
		offsetX = levelFactory.collisionLayer.offsetX / (1 / RenderingSystem.PIXELS_TO_METRES)
		blankTile = new StaticTiledMapTile(DFUtils.makeTextureRegion(8, 8, '#000000'))
		blankTile.properties.put('node', true)
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
			//I'm just sticking this here because I don't want to have an entire system just for this one piece
			PowerUpComponent powerUpComponent = levelFactory.powerCom.get(entity)
			if(powerUpComponent && powerUpComponent.activeTime > 0) {
				powerUpComponent.activeTime -= deltaTime
				if(powerUpComponent.activeTime <= 0) {
					levelFactory.powerUp.stop(levelFactory.powerUpId)
				}
			}

			if (collidedEntity) {
				SdBodyComponent collidedBody = Mapper.bCom.get(collidedEntity)
				SdBodyComponent body = Mapper.bCom.get(entity)

				switch (collidedType) {
					case TypeComponent.TYPES.OTHER:
						int worth = Mapper.scoreCom.get(collidedEntity).worth
						collidedBody.isDead = true
						levelFactory.playerScore = levelFactory.playerScore + worth
						break

					case TypeComponent.TYPES.BLINKY:
					case TypeComponent.TYPES.PINKY:
					case TypeComponent.TYPES.INKY:
					case TypeComponent.TYPES.CLYDE:
						if(powerUpComponent && powerUpComponent.activeTime > 0) {
							int worth = Mapper.scoreCom.get(collidedEntity).worth
							collidedBody.isDead = true
							levelFactory.playerScore = levelFactory.playerScore + worth
						} else {
							body.isDead = true
							levelFactory.gameOverPacMan.play()
						}

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
					collisionX = levelFactory.isCellBlocked(pos.x - playerWidth as float, pos.y)
				} else if(isGoingRight) {
					collisionX = levelFactory.isCellBlocked(pos.x + playerWidth as float, pos.y)
				}

				if(isGoingDown) {
					collisionY = levelFactory.isCellBlocked(pos.x, pos.y - playerHeight as float)
				} else if(isGoingUp) {
					collisionY = levelFactory.isCellBlocked(pos.x, pos.y + playerHeight as float)
				}

				if(collisionX || (collisionY && body.body.linearVelocity.x)) {
					Mapper.texCom.get(entity).animation.playMode = Animation.PlayMode.NORMAL
					//Reverse the velocity
					float xVel = body.body.linearVelocity.x * -1 as float
					body.body.setLinearVelocity(0, 0)

					while(levelFactory.isCellBlocked(body.body.position.x, body.body.position.y)) {
						float newX = body.body.position.x + (xVel * deltaTime) as float
						body.body.setTransform(newX, body.body.position.y, 0)
					}
				}

				if(collisionY || (collisionX && body.body.linearVelocity.y)) {
					Mapper.texCom.get(entity).animation.playMode = Animation.PlayMode.NORMAL
					//Reverse the velocity
					float yVel = body.body.linearVelocity.y * -1 as float
					body.body.setLinearVelocity(0, 0)

					while(levelFactory.isCellBlocked(body.body.position.x, body.body.position.y)) {
						float newY = body.body.position.y + (yVel * deltaTime) as float
						body.body.setTransform(body.body.position.x, newY, 0)
					}
				}

				//Pellet checking
				Vector2 tilePos = levelFactory.tilePosition(body.body.position.x, body.body.position.y)
				boolean overPellet = levelFactory.isCell('pellet', true, tilePos)
				boolean overPowerUp = levelFactory.isCell('powerup', true, tilePos)
				if(overPellet || overPowerUp) {
					Vector2 tileGamePos = levelFactory.gamePosition(tilePos.x as int, tilePos.y as int)
					def tileCenterX = tileGamePos.x
					def tileCenterY = tileGamePos.y
					if(Math.abs(body.body.position.x - tileCenterX) < 0.07 && Math.abs(body.body.position.y - tileCenterY) < 0.07) {
						//Remove tile, showing black background
						levelFactory.getCell(tilePos).setTile(blankTile)

						if(overPellet) {
							levelFactory.playerScore += 10
							levelFactory.nextPelletSound.play()
							if(levelFactory.nextPelletSound == levelFactory.eatPelletA) {
								levelFactory.nextPelletSound = levelFactory.eatPelletB
							} else {
								levelFactory.nextPelletSound = levelFactory.eatPelletA
							}
						}
						if(overPowerUp) {
							levelFactory.playerScore += 20
							if(!powerUpComponent) {
								powerUpComponent = engine.createComponent(PowerUpComponent)
								powerUpComponent.activeTime = 10
								entity.add(powerUpComponent)
							} else {
								powerUpComponent.activeTime += 10
							}

							if(levelFactory.powerUpId) {
								levelFactory.powerUp.stop(levelFactory.powerUpId)
							}
							levelFactory.powerUpId = levelFactory.powerUp.play()
						}
					}
				}
			}
		}
	}
}
