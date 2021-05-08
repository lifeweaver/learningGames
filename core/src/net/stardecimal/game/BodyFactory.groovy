package net.stardecimal.game

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.CircleShape
import com.badlogic.gdx.physics.box2d.EdgeShape
import com.badlogic.gdx.physics.box2d.FixtureDef
import com.badlogic.gdx.physics.box2d.PolygonShape
import com.badlogic.gdx.physics.box2d.Shape
import com.badlogic.gdx.physics.box2d.World

class BodyFactory {
	private World world
	private static BodyFactory thisInstance
	private final float DEGTORAD = 0.0174533f

	static final int STEEL = 0
	static final int WOOD = 1
	static final int RUBBER = 2
	static final int STONE = 3
	static final int PING_PONG = 4
	static final int NOTHING = 5

	BodyFactory(World world) {
		this.world = world
		thisInstance = this
	}

	void dispose() {
		this.world = null
		thisInstance = null
	}

	static BodyFactory getInstance(World world) {
		if (thisInstance == null) {
			thisInstance = new BodyFactory(world)
		}
		return thisInstance
	}

	static FixtureDef makeFixture(int material, Shape shape, boolean isSensor=false) {
		FixtureDef fixtureDef = new FixtureDef()
		fixtureDef.shape = shape

		switch (material) {
			case STEEL:
				fixtureDef.density = 1f
				fixtureDef.friction = 0.3f
				fixtureDef.restitution = 0.1f
				break
			case WOOD:
				fixtureDef.density = 0.5f
				fixtureDef.friction = 0.7f
				fixtureDef.restitution = 0.3f
				break
			case RUBBER:
				fixtureDef.density = 1f
				fixtureDef.friction = 0f
				fixtureDef.restitution = 1f
				break
			case STONE:
				fixtureDef.density = 1f
				fixtureDef.friction = 0.5f
				fixtureDef.restitution = 0f
				break
			case PING_PONG:
				fixtureDef.density = 0.00000000001f
				fixtureDef.friction = 0f
				fixtureDef.restitution = 1f
				break
			case NOTHING:
				fixtureDef.density = 0
				fixtureDef.friction = 0
				fixtureDef.restitution = 0
				break
			default:
				fixtureDef.density = 7f
				fixtureDef.friction = 0.5f
				fixtureDef.restitution = 0.3f
		}
		fixtureDef.isSensor = isSensor
		return fixtureDef
	}

	// https://rotatingcanvas.com/edge-shape-in-box2d/
	Body makeEdgeBody(float startPosX, float startPosY, float endPosX, float endPosY, BodyDef.BodyType bodyType) {
		BodyDef boxBodyDef = new BodyDef()
		boxBodyDef.type = bodyType

		//CALCULATE CENTER OF LINE SEGMENT
		float posX =(startPosX+endPosX) / 2f as float
		float posY =(startPosY+endPosY) / 2f as float
		
		//CALCULATE LENGTH OF LINE SEGMENT
		float len = (float) Math.sqrt((startPosX - endPosX) * (startPosX - endPosX) + (startPosY - endPosY) * (startPosY - endPosY))

		boxBodyDef.position.set(posX, posY);
		boxBodyDef.angle = 0

		Body boxBody = world.createBody(boxBodyDef)

		//ADD EDGE FIXTURE TO BODY
		makeEdgeShape(boxBody, len ,1 ,0 ,1)

		//CALCULATE ANGLE OF THE LINE SEGMENT
		boxBody.setTransform(posX, posY, MathUtils.atan2(endPosY - startPosY as float, endPosX - startPosX as float))

		return boxBody
	}

	Body makeCirclePolyBody(float posX, float posY, float radius, int material, BodyDef.BodyType bodyType, boolean fixedRotation=false, boolean isSensor=false) {
		BodyDef boxBodyDef = new BodyDef()
		boxBodyDef.type = bodyType
		boxBodyDef.position.x = posX
		boxBodyDef.position.y = posY
		boxBodyDef.fixedRotation = fixedRotation

		Body boxBody = world.createBody(boxBodyDef)
		CircleShape circleShape = new CircleShape()
		circleShape.setRadius(radius / 2 as float)
		boxBody.createFixture(makeFixture(material, circleShape, isSensor))
		circleShape.dispose()
		return boxBody
	}

	private static void makeEdgeShape(Body body, float len, float density, float restitution, float friction) {
		FixtureDef fixtureDef = new FixtureDef()
		fixtureDef.density = density
		fixtureDef.restitution = restitution
		fixtureDef.friction = friction

		EdgeShape edgeShape = new EdgeShape()

		//SETTING THE POINTS AS OFFSET DISTANCE FROM CENTER
		edgeShape.set(-len / 2f as float, 0, len / 2f as float, 0)
		fixtureDef.shape = edgeShape

		body.createFixture(fixtureDef)
		fixtureDef.shape.dispose()
	}

	Body makeBoxPolyBody(float posX, float posY, float width, float height, int material, BodyDef.BodyType bodyType, boolean fixedRotation=false, boolean isSensor=false) {
		// create a definition
		BodyDef boxBodyDef = new BodyDef()
		boxBodyDef.type = bodyType
		boxBodyDef.position.x = posX
		boxBodyDef.position.y = posY
		boxBodyDef.fixedRotation = fixedRotation

		//create the body to attach said definition
		Body boxBody = world.createBody(boxBodyDef)
		PolygonShape poly = new PolygonShape()
		poly.setAsBox(width / 2 as float, height / 2 as float)
		boxBody.createFixture(makeFixture(material, poly, isSensor))
		poly.dispose()

		return boxBody
	}

	Body makePolygonShapeBody(Vector2[] vertices, float posX, float posY, int material, BodyDef.BodyType bodyType){
		BodyDef boxBodyDef = new BodyDef()
		boxBodyDef.type = bodyType
		boxBodyDef.position.x = posX
		boxBodyDef.position.y = posY
		Body boxBody = world.createBody(boxBodyDef)

		PolygonShape polygon = new PolygonShape()
		polygon.set(vertices)
		boxBody.createFixture(makeFixture(material,polygon))
		polygon.dispose()

		return boxBody
	}

	void makeConeSensor(Body body, float size){

		FixtureDef fixtureDef = new FixtureDef()
		//fixtureDef.isSensor = true; // will add in future

		PolygonShape polygon = new PolygonShape()

		float radius = size
		Vector2[] vertices = new Vector2[5]
		vertices[0] = new Vector2(0,0)
		for (int i = 2; i < 6; i++) {
			float angle = (float) (i  / 6.0 * 145 * DEGTORAD) // convert degrees to radians
			vertices[i-1] = new Vector2( radius * ((float)Math.cos(angle)) as float, radius * ((float)Math.sin(angle)) as float)
		}
		polygon.set(vertices);
		fixtureDef.shape = polygon;
		body.createFixture(fixtureDef);
		polygon.dispose();
	}

	void makeAllFixturesSensors(Body body) {
		body.fixtureList.each {
			it.setSensor(true)
		}
	}

}