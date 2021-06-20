package net.stardecimal.game.entity.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.ai.steer.Steerable
import com.badlogic.gdx.ai.steer.SteeringAcceleration
import com.badlogic.gdx.ai.steer.SteeringBehavior
import com.badlogic.gdx.ai.utils.Location
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.utils.Pool
import net.stardecimal.game.util.DFUtils

class SteeringComponent implements Steerable<Vector2>, Component, Pool.Poolable {

	static enum SteeringState {WANDER,SEEK,FLEE,ARRIVE,NONE,INTERPOSE} 	// a list of possible behaviours
	SteeringState currentMode = SteeringState.WANDER 	// stores which state the entity is currently in
	Body body	// stores a reference to our Box2D body

	// Steering data
	float maxLinearSpeed = 1.5f	// stores the max speed the entity can go
	float maxLinearAcceleration = 5f	// stores the max acceleration
	float maxAngularSpeed = 50f		// the max turning speed
	float maxAngularAcceleration = 5f // the max turning acceleration
	float zeroThreshold = 0.1f	// how accurate should checks be (0.0000001f will mean the entity must get within 0.0000001f of
	// target location. This will cause problems as our entities travel pretty fast and can easily over or undershoot this.)
	SteeringBehavior<Vector2> steeringBehavior // stores the action behaviour
	private static final SteeringAcceleration<Vector2> steeringOutput = new SteeringAcceleration<Vector2>(new Vector2()) // this is the actual steering vector for our unit
	private float boundingRadius = 1f   // the minimum radius size for a circle required to cover whole object
	private boolean tagged = true		// This is a generic flag utilized in a variety of ways. (never used this myself)
	private boolean independentFacing = true // defines if the entity can move in a direction other than the way it faces)

	@Override
	void reset() {
		println('SteeringComponent - reset called')
		currentMode = SteeringState.NONE
		body = null
		steeringBehavior = null
		steeringOutput.linear = new Vector2()
		steeringOutput.angular = 0f
		maxLinearSpeed = 1.5f
		maxLinearAcceleration = 5f
		maxAngularSpeed = 50f
		maxAngularAcceleration = 5f
		zeroThreshold = 0.1f
		boundingRadius = 1f
		tagged = true
		independentFacing = true
	}

	boolean isIndependentFacing () {
		return independentFacing
	}

	void setIndependentFacing (boolean independentFacing) {
		this.independentFacing = independentFacing
	}

	/** Call this to update the steering behaviour (per frame)
	 * @param delta delta time between frames
	 */
	void update (float delta) {
		//TODO: find a better way to stop this if either the 'agentA' or 'agentB' steeringBehavior is null aka has been reset.
		if (steeringBehavior != null) {
			try {
				steeringBehavior.calculateSteering(steeringOutput)
				applySteering(steeringOutput, delta)
			} catch(Exception e) {
//				println("Exception caught: ${e}")
//				e.printStackTrace()
			}
		}
	}

	/** apply steering to the Box2d body
	 * @param steering the steering vector
	 * @param deltaTime teh delta time
	 */
	protected void applySteering (SteeringAcceleration<Vector2> steering, float deltaTime) {
		boolean anyAccelerations = false

		// Update position and linear velocity.
		if (!steering.linear.isZero()) {
			// this method internally scales the force by deltaTime
//			Vector2 force = steering.linear.scl(deltaTime)

			//TODO: figure out why the the applyForceToCenter doesn't work
//			body.applyForceToCenter(force, true)
			body.setLinearVelocity(steering.linear.x, steering.linear.y)
			anyAccelerations = true
		}

		// Update orientation and angular velocity
		if (isIndependentFacing()) {
			if (steering.angular != 0) {
				// this method internally scales the torque by deltaTime
				body.applyTorque(steering.angular, true)
				anyAccelerations = true
			}
		} else {
			// If we haven't got any velocity, then we can do nothing.
			Vector2 linVel = getLinearVelocity()
			if (!linVel.isZero(getZeroLinearSpeedThreshold())) {
				float newOrientation = vectorToAngle(linVel)
				body.setAngularVelocity((newOrientation - getAngularVelocity()) * deltaTime as float) // this is superfluous if independentFacing is always true
				body.setTransform(body.getPosition(), newOrientation)
			}
		}

		if (anyAccelerations) {
			// Cap the linear speed
			Vector2 velocity = body.getLinearVelocity()
			float currentSpeedSquare = velocity.len2()

			if (currentSpeedSquare > (maxLinearSpeed * maxLinearSpeed)) {
				body.setLinearVelocity(velocity.scl(maxLinearSpeed / Math.sqrt(currentSpeedSquare) as float))
			}
			// Cap the angular speed
			float maxAngVelocity = getMaxAngularSpeed()
			if (body.getAngularVelocity() > maxAngVelocity) {
				body.setAngularVelocity(maxAngVelocity)
			}
		}
	}

	@Override
	Vector2 getPosition() {
		return body.position
	}

	@Override
	float getOrientation() {
		return body.getAngle()
	}

	@Override
	void setOrientation(float orientation) {
		body.setTransform(getPosition(), orientation)
	}
	@Override
	float vectorToAngle(Vector2 vector) {
		return DFUtils.vectorToAngle(vector)
	}
	@Override
	Vector2 angleToVector(Vector2 outVector, float angle) {
		return DFUtils.angleToVector(outVector, angle)
	}
	@Override
	Location<Vector2> newLocation() {
		return new SdLocation()
	}
	@Override
	float getZeroLinearSpeedThreshold() {
		return zeroThreshold
	}
	@Override
	void setZeroLinearSpeedThreshold(float value) {
		zeroThreshold = value
	}
	@Override
	float getMaxLinearSpeed() {
		return this.maxLinearSpeed
	}
	@Override
	void setMaxLinearSpeed(float maxLinearSpeed) {
		this.maxLinearSpeed = maxLinearSpeed
	}
	@Override
	float getMaxLinearAcceleration() {
		return this.maxLinearAcceleration
	}
	@Override
	void setMaxLinearAcceleration(float maxLinearAcceleration) {
		this.maxLinearAcceleration = maxLinearAcceleration
	}
	@Override
	float getMaxAngularSpeed() {
		return this.maxAngularSpeed
	}
	@Override
	void setMaxAngularSpeed(float maxAngularSpeed) {
		this.maxAngularSpeed = maxAngularSpeed
	}
	@Override
	float getMaxAngularAcceleration() {
		return this.maxAngularAcceleration
	}
	@Override
	void setMaxAngularAcceleration(float maxAngularAcceleration) {
		this.maxAngularAcceleration = maxAngularAcceleration
	}
	@Override
	Vector2 getLinearVelocity() {
		return body.getLinearVelocity()
	}
	@Override
	float getAngularVelocity() {
		return body.getAngularVelocity()
	}
	@Override
	float getBoundingRadius() {
		return this.boundingRadius
	}
	@Override
	boolean isTagged() {
		return this.tagged
	}
	@Override
	void setTagged(boolean tagged) {
		this.tagged = tagged
	}
}