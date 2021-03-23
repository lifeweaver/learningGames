package net.stardecimal.game.pong.bodies

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.CircleShape
import com.badlogic.gdx.physics.box2d.FixtureDef
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.graphics.Texture
import net.stardecimal.game.SizingUtil

class PingPong extends Image {
	Body body
	World world

	PingPong(World world1, float  pos_x, float pos_y, float width, float height, float angle) {
		super(new Texture("pong/pingPong.png"))
//		pos_x = SizingUtil.convertToWorld(pos_x)
//		pos_y = SizingUtil.convertToWorld(pos_y)
		width = SizingUtil.convertToWorld(width)
		height = SizingUtil.convertToWorld(height)
		this.setSize(width, height)
		this.setOrigin(this.width/2 as float, this.height/2 as float)
		this.rotateBy(angle)
		this.setPosition(pos_x, pos_y)
		world = world1


		BodyDef bodyDef = new BodyDef()
		bodyDef.type = BodyDef.BodyType.DynamicBody
		bodyDef.position.set(pos_x, pos_y)

		// Create body from the definition and add it to the world
		body = world.createBody(bodyDef)

		CircleShape shape = new CircleShape()
		shape.setRadius(width / 2 as float)

		FixtureDef fixtureDef = new FixtureDef()
		fixtureDef.shape = shape
		fixtureDef.density = 0f
		fixtureDef.friction = 0f
		fixtureDef.restitution = 1f

		body.createFixture(fixtureDef)

		shape.dispose()
	}

	@Override
	void act(float delta) {
		super.act(delta)

		this.setRotation((float) (body.angle *  MathUtils.radiansToDegrees))
		this.setPosition(body.position.x-this.width/2 as float, body.position.y-this.height/2 as float)
	}

	@Override
	void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha)
	}
}
