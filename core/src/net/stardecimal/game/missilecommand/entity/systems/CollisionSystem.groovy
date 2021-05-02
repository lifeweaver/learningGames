package net.stardecimal.game.missilecommand.entity.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.Vector2
import net.stardecimal.game.MyGames
import net.stardecimal.game.entity.components.CollisionComponent
import net.stardecimal.game.entity.components.Mapper
import net.stardecimal.game.entity.components.SdBodyComponent
import net.stardecimal.game.entity.components.TypeComponent
import net.stardecimal.game.missilecommand.LevelFactory
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class CollisionSystem extends IteratingSystem {
	final MyGames parent
	final LevelFactory levelFactory
	private static final Logger log = LoggerFactory.getLogger(CollisionSystem)

	@SuppressWarnings('unchecked')
	CollisionSystem(MyGames game, LevelFactory lvlFactory) {
		super(Family.all(CollisionComponent.class).get())
		parent = game
		levelFactory = lvlFactory
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		CollisionComponent cc = Mapper.collisionCom.get(entity)
		TypeComponent thisType = Mapper.typeCom.get(entity)
		Entity collidedEntity = cc.collisionEntity

		if(collidedEntity) {
			switch(thisType.type) {
				case TypeComponent.TYPES.BULLET:
					handleBullet(entity, collidedEntity, cc)
					break

				case TypeComponent.TYPES.EXPLOSION:
					handleExplosion(entity, collidedEntity, cc)
					break

				default:
					log.debug("Unhandled type: ${thisType.type}, collidedEntity: ${collidedEntity}, entityProps: ${entity.properties}")
			}
		}
	}

	void handleBullet(Entity entity, Entity collidedEntity, CollisionComponent cc) {
		SdBodyComponent body = Mapper.bCom.get(entity)
		TypeComponent type = Mapper.typeCom.get(collidedEntity)
		Vector2 boom = new Vector2(body.body.position.x, body.body.position.y)
		if (type) {
			switch (type.type) {
				case TypeComponent.TYPES.SCENERY:
					levelFactory.createBoom(boom)
					body.isDead = true
					break
				case TypeComponent.TYPES.CITY:
				case TypeComponent.TYPES.DEFENDER_MISSILE:
				case TypeComponent.TYPES.EXPLOSION:
					SdBodyComponent collidedBody = Mapper.bCom.get(collidedEntity)
					levelFactory.createBoom(boom)
					collidedBody.isDead = true
					body.isDead = true
					break

				default:
					log.debug("bullet collided with something: ${TypeComponent.getTypeName(type.type)}")
			}

			cc.collisionEntity = null // collision handled reset component
		} else {
			if(Mapper.peCom.get(collidedEntity) && collidedEntity.components.size() == 1) {
				//Ignore, we don't care if our bullet collides with other particles if it only has one component
			} else if(collidedEntity.components.isEmpty()) {
				//Ignore if the collided entity has no components, who knows what it is or was.
			} else {
				log.debug("type1 == null, ${collidedEntity.properties}")
			}
		}
	}

	void handleExplosion(Entity entity, Entity collidedEntity, CollisionComponent cc) {
		SdBodyComponent body = Mapper.bCom.get(entity)
		TypeComponent type = Mapper.typeCom.get(collidedEntity)
		if(type) {
			SdBodyComponent collidedBody = Mapper.bCom.get(collidedEntity)
			Vector2 boom = new Vector2(collidedBody.body.position.x, collidedBody.body.position.y)
			switch (type.type) {
				case TypeComponent.TYPES.BULLET:
					levelFactory.createBoom(boom)
					collidedBody.isDead = true
					break

				case TypeComponent.TYPES.DEFENDER_MISSILE:
					levelFactory.createBoom(boom)
					collidedBody.isDead = true
					break

				case TypeComponent.TYPES.EXPLOSION: //Ignore other explosions
					break

				case TypeComponent.TYPES.CITY:
					collidedBody.isDead = true
					break

				default:
					log.debug("explosion collided with something: ${type.type}")
			}

			cc.collisionEntity = null // collision handled reset component
		} else {
			if(Mapper.peCom.get(collidedEntity) && collidedEntity.components.size() == 1) {
				//Ignore, we don't care if our explosion collides with other particles
			} else if(collidedEntity.components.isEmpty()) {
				//Ignore if the collided entity has no components, who knows what it is or was.
			} else {
				log.debug("type2 == null, ${collidedEntity.properties}")
			}
		}
	}
}


