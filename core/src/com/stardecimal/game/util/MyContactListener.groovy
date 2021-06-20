package com.stardecimal.game.util

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.physics.box2d.Contact
import com.badlogic.gdx.physics.box2d.ContactImpulse
import com.badlogic.gdx.physics.box2d.ContactListener
import com.badlogic.gdx.physics.box2d.Fixture
import com.badlogic.gdx.physics.box2d.Manifold
import com.stardecimal.game.entity.components.CollisionComponent
import com.stardecimal.game.entity.util.Mapper
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class MyContactListener implements ContactListener {
	private static final Logger log = LoggerFactory.getLogger(this)

	MyContactListener() {
	}

	@Override
	void beginContact(Contact contact) {
		Fixture fa = contact.fixtureA
		Fixture fb = contact.fixtureB

		if(fa.body.userData instanceof Entity) {
			Entity ent = fa.body.userData as Entity
			entityCollision(ent, fb)
			return
		} else if(fb.body.userData instanceof Entity) {
			Entity ent = fb.body.userData as Entity
			entityCollision(ent, fa)
			return
		}
	}

	private void entityCollision(Entity entity, Fixture fb) {
		if(fb.body.userData instanceof Entity) {
			Entity colEnt = fb.body.userData as Entity
//			log.debug("entity${Integer.toHexString(entity.hashCode())}.type: ${TypeComponent.getTypeName(Mapper.typeCom.get(entity)?.type)}, colEnt${Integer.toHexString(colEnt.hashCode())}.type: ${TypeComponent.getTypeName(Mapper.typeCom.get(colEnt)?.type)}")

			CollisionComponent col = Mapper.collisionCom.get(entity)
			CollisionComponent colb = Mapper.collisionCom.get(colEnt)

			if(col) {
				col.collisionEntity = colEnt
			} else if(colb) {
				colb.collisionEntity = entity
			}
		}
	}

	@Override
	void endContact(Contact contact) {
		//Should I remove the CollisionsComponent.collisionEntity so I can use it to keep track of what is touching what?
		//How about something touching multiple things?
		//This method would only work for one object, I probably need a list and I just add or remove entities to that list
		//So I know which entities are touching each other. I'd also probably have to check if they are dead.
		//Especially since the entities are reused.
		//Turns out you should use a sensor to keep track of what is currently touching? http://www.iforce2d.net/b2dtut/sensors
		//Use a Set, when storing the objects, it should keep you from adding duplicates
	}

	@Override
	void preSolve(Contact contact, Manifold oldManifold) {
	}

	@Override
	void postSolve(Contact contact, ContactImpulse impulse) {
	}
}
