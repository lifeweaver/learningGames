package net.stardecimal.game

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.utils.Array
import net.stardecimal.game.loader.SdAssetManager

trait DefaultLevelFactory {
	BodyFactory bodyFactory
	World world
	PooledEngine engine
	SdAssetManager assetManager
	Entity player

	void init(PooledEngine en, SdAssetManager am) {
		engine = en
		assetManager = am

		// the y is gravity, normal is -9.8f I think.
		world = new World(new Vector2(0, 0), true)
		world.setContactListener(new MyContactListener())
		bodyFactory = BodyFactory.getInstance(world)
	}

	void resetWorld() {
		Array<Body> bodies = new Array<>()
		world.getBodies(bodies)
		bodies.each {
			world.destroyBody(it)
		}
	}
}