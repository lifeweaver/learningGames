package net.stardecimal.game.entity.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer
import com.badlogic.gdx.physics.box2d.World

class PhysicsDebugSystem extends IteratingSystem {
	private Box2DDebugRenderer debugRenderer
	private World world
	private OrthographicCamera camera
	private boolean debug = true

	@SuppressWarnings('unchecked')
	PhysicsDebugSystem(World world, OrthographicCamera camera) {
		super(Family.all().get())
		debugRenderer = new Box2DDebugRenderer()
		this.world = world
		this.camera = camera
		this.priority = 999
	}

	@Override
	void update(float deltaTime) {
		super.update(deltaTime)
		if(debug) debugRenderer.render(world, camera.combined)
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {

	}
}
