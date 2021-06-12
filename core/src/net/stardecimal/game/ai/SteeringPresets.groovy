package net.stardecimal.game.ai

import com.badlogic.gdx.ai.steer.behaviors.Arrive
import com.badlogic.gdx.ai.steer.behaviors.Flee
import com.badlogic.gdx.ai.steer.behaviors.Interpose
import com.badlogic.gdx.ai.steer.behaviors.Seek
import com.badlogic.gdx.ai.steer.behaviors.Wander
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import net.stardecimal.game.entity.components.SdLocation
import net.stardecimal.game.entity.components.SteeringComponent

class SteeringPresets {
	static Wander<Vector2> getWander(SteeringComponent scom) {
		Wander<Vector2> wander = new Wander<Vector2>(scom)
				.setFaceEnabled(false) // let wander behaviour manage facing
				.setWanderOffset(5f) // distance away from entity to set target
				.setWanderOrientation(180f) // the initial orientation
				.setWanderRadius(10f) // size of target
				.setWanderRate(MathUtils.PI2 * 4 as float) // higher values = more spinning
		return wander
	}

	static Seek<Vector2> getSeek(SteeringComponent seeker, SdLocation target) {
		Seek<Vector2> seek = new Seek<Vector2>(seeker,target)
		return seek
	}

	static Seek<Vector2> getSeek(SteeringComponent seeker, SteeringComponent target) {
		Seek<Vector2> seek = new Seek<Vector2>(seeker,target)
		return seek
	}

	static Interpose<Vector2> getInterpose(SteeringComponent owner, SteeringComponent interposer, SteeringComponent target) {
		Interpose<Vector2> interpose = new Interpose<Vector2>(owner, interposer, target, 0.5f)
				.setTimeToTarget(0.1f)
				.setArrivalTolerance(0.001f)
				.setDecelerationRadius(20)

		return interpose
	}


	static Flee<Vector2> getFlee(SteeringComponent runner, SteeringComponent fleeingFrom) {
		Flee<Vector2> flee = new Flee<Vector2>(runner, fleeingFrom)
		return flee
	}

	static Flee<Vector2> getFlee(SteeringComponent runner, SdLocation fleeingFrom) {
		Flee<Vector2> flee = new Flee<Vector2>(runner, fleeingFrom)
		return flee
	}

	static Arrive<Vector2> getArrive(SteeringComponent runner, SteeringComponent target) {
		Arrive<Vector2> arrive = new Arrive<Vector2>(runner, target)
				.setTimeToTarget(0.1f) // default 0.1f
				.setArrivalTolerance(7f) //
				.setDecelerationRadius(10f)

		return arrive
	}

	static Arrive<Vector2> getArrive(SteeringComponent runner, SdLocation target) {
		Arrive<Vector2> arrive = new Arrive<Vector2>(runner, target)
				.setTimeToTarget(0.1f) // default 0.1f
				.setArrivalTolerance(7f) //
				.setDecelerationRadius(10f)

		return arrive
	}
}
