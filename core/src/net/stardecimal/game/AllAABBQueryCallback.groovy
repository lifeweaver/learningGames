package net.stardecimal.game

import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.Fixture
import com.badlogic.gdx.physics.box2d.QueryCallback
import com.badlogic.gdx.utils.Array

class AllAABBQueryCallback implements QueryCallback {
	Array<Body> collisionBodies = new Array<Body>()

	@Override
	boolean reportFixture (Fixture fixture) {

		if(!collisionBodies.contains(fixture.getBody(),true)) {
			collisionBodies.add(fixture.getBody())
		}
		return true
	}
}
