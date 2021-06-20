package com.stardecimal.game.util

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.Fixture
import com.badlogic.gdx.physics.box2d.RayCastCallback
import com.badlogic.gdx.utils.Array
import com.stardecimal.game.entity.systems.RenderingSystem

class SeeThroughRayCastCallback implements RayCastCallback {
	float  BOX_TO_WORLD = RenderingSystem.PPM //As our box to world ratio is  1:100
	Vector2 collisionPoint
	boolean isColliding
	Array<Body> collisionBodies

	SeeThroughRayCastCallback(Array<Body> collisionBodies, Vector2 ep){
		this.collisionBodies = collisionBodies
		collisionPoint = new Vector2(ep.x,ep.y).scl(BOX_TO_WORLD)
		isColliding = false
	}

	Vector2 getCollisionPoint(){
		return collisionPoint
	}

	boolean didRayCollide(){
		return isColliding
	}

	@Override
	float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
		//return -1 - to ignore the current fixture
		//return 0 - to terminate the raycast
		//return fraction - to clip the raycast at current point
		//return 1 - don't clip the ray and continue
		isColliding = true
		collisionPoint.set(point).scl(BOX_TO_WORLD)
		if(!collisionBodies.contains(fixture.getBody(),true)) {
			collisionBodies.add(fixture.getBody())
		}

		//Always ignore the fixture so we get everything in the circle.
		return -1
	}
}