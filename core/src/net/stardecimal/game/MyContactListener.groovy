package net.stardecimal.game

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.physics.box2d.Contact
import com.badlogic.gdx.physics.box2d.ContactImpulse
import com.badlogic.gdx.physics.box2d.ContactListener
import com.badlogic.gdx.physics.box2d.Fixture
import com.badlogic.gdx.physics.box2d.Manifold
import net.stardecimal.game.entity.components.CollisionComponent
import net.stardecimal.game.entity.components.Mapper

class MyContactListener implements ContactListener {

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

			Mapper.collisionCom
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
	}

	@Override
	void preSolve(Contact contact, Manifold oldManifold) {
	}

	@Override
	void postSolve(Contact contact, ContactImpulse impulse) {
	}
}
