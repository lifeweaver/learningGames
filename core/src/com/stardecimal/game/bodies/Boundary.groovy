package com.stardecimal.game.bodies

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.PolygonShape
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.stardecimal.game.util.SizingUtil


class Boundary extends Image {
	Body body
	World world

	Boundary(World world1, float  pos_x, float pos_y, float width, float height) {
//		pos_x = SizingUtil.convertToWorld(pos_x)
//		pos_y = SizingUtil.convertToWorld(pos_y)
		width = SizingUtil.convertToWorld(width)
		height = SizingUtil.convertToWorld(height)
		// no need for a texture for this
//		super(new Texture(Gdx.files.internal("wood.jpg")));
		this.setSize(width, height)
		this.setOrigin(this.width/2 as float, this.height/2 as float)
		this.setPosition(pos_x, pos_y)
		world = world1
		BodyDef groundBodyDef = new BodyDef()

		groundBodyDef.position.set(new Vector2(pos_x, pos_y))

		// Create body from the definition and add it to the world
		body = world.createBody(groundBodyDef)

		PolygonShape groundBox = new PolygonShape()
		groundBox.setAsBox(this.width/2 as float, this.height/2 as float)
		body.setTransform(this.x + this.width/2 as float, this.y + this.height/2 as float, 0)

		body.createFixture(groundBox, 0.0f)

		groundBox.dispose()
	}

	@Override
	void act(float delta) {
		super.act(delta)
	}

	@Override
	void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha)
	}

}
