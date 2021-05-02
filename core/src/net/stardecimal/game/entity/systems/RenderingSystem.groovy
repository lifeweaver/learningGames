package net.stardecimal.game.entity.systems

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.SortedIteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Fixture
import com.badlogic.gdx.physics.box2d.PolygonShape
import com.badlogic.gdx.physics.box2d.Shape
import com.badlogic.gdx.utils.Array
import net.stardecimal.game.entity.components.Mapper
import net.stardecimal.game.entity.components.SdBodyComponent
import net.stardecimal.game.entity.components.TextureComponent
import net.stardecimal.game.entity.components.TransformComponent

/**
 * This code was adapted from code created by Barry @ https://github.com/RoaringCatGames
 * by the tutorial maker in https://www.gamedevelopment.blog/full-libgdx-game-tutorial-entities-ashley/
 * and further used by me after going through the tutorial
 */

class RenderingSystem extends SortedIteratingSystem {
	static final float PPM = 16.0f //amount of pixels each metre of box2d objects contains

	// this gets the height and width of our camera frustrum based off the width and height of the screen and our pixel per meter ratio
	static final float FRUSTUM_WIDTH = Gdx.graphics.width / PPM
	static final float FRUSTUM_HEIGHT = Gdx.graphics.height / PPM

	static final float PIXELS_TO_METRES = 1.0f // get the ration for converting pixels to metres

	private static Vector2 meterDimensions = new Vector2()
	private static Vector2 pixelDimensions = new Vector2()

	static Vector2 getScreenSizeInMeters() {
		meterDimensions.set(Gdx.graphics.width * PIXELS_TO_METRES as float, Gdx.graphics.height * PIXELS_TO_METRES as float)
		return  meterDimensions
	}

	static Vector2 getScreenSizeInPixes() {
		pixelDimensions.set(Gdx.graphics.width, Gdx.graphics.height)
		return pixelDimensions
	}

	// convenience method
	static float pixelsToMeters(float pixelValue) {
		return pixelValue * PIXELS_TO_METRES
	}

	private SpriteBatch batch
	private Array<Entity> renderQueue // used to allow sorting of images allowing us to draw images on top of each other
	private Comparator<Entity> comparator // to sort images based on the z position of the transformComponent
	private OrthographicCamera cam
	private TiledMap background
	private OrthogonalTiledMapRenderer backgroundRenderer

	@SuppressWarnings('unchecked')
	RenderingSystem(SpriteBatch batch) {
		super(Family.all(TransformComponent.class, TextureComponent.class).get(), new ZComparator())
		priority = 9
		renderQueue = new Array<Entity>()

		this.batch = batch

		cam = new OrthographicCamera(FRUSTUM_WIDTH, FRUSTUM_HEIGHT)
		cam.position.set(FRUSTUM_WIDTH / 2f as float, FRUSTUM_HEIGHT / 2f as float, 0)
	}

	void addTiledMapBackground(TiledMap map) {
		if(map) {
			background = map
			backgroundRenderer = new OrthogonalTiledMapRenderer(background, batch)
		}
	}

	@Override
	void update(float deltaTime) {
		super.update(deltaTime)

		// sort the renderQueue based on the z index
//		renderQueue.sort(comparator)
		// for some reason the renderQueue.sort(comparator) throws a null pointer not sure why
		ComponentMapper<TransformComponent> cmTrans = ComponentMapper.getFor(TransformComponent.class)
		renderQueue.sort {
			cmTrans.get(it).position.z
		}

		// update camera and sprite batch
		cam.update()
		if(backgroundRenderer) {
			backgroundRenderer.setView(cam)
			backgroundRenderer.render()
		}

		batch.setProjectionMatrix(cam.combined)
//		batch.enableBlending()
		batch.begin()

		renderQueue.each { Entity entity ->
			TextureComponent tex = Mapper.texCom.get(entity)
			TransformComponent t = Mapper.transCom.get(entity)
			SdBodyComponent b = Mapper.bCom.get(entity)

			if((tex.region != null || tex.texture != null) && !t.isHidden) {
				float width, height

				if(tex.region) {
					width = tex.region.regionWidth
					height = tex.region.regionHeight
				} else {
					width = tex.texture.width
					height = tex.texture.height
				}

				float w2 = 0f
				float h2 = 0f
				b.body.fixtureList.each { Fixture fixture ->
					if(fixture) {
						if(fixture.shape.type == Shape.Type.Circle) {
							w2 += fixture.shape.radius * 4
							h2 += fixture.shape.radius * 4
						} else if(fixture.shape.type == Shape.Type.Polygon) {
							PolygonShape shape = fixture.shape as PolygonShape
							Vector2 vector = new Vector2()
//
							def vectors = []
							shape.vertexCount.times {
								shape.getVertex(it, vector)
								vectors.add(new Vector2(vector.x, vector.y))
							}

							vectors.sort {it.x}
							w2 = (vectors.first().x).abs() + (vectors.last().x).abs()

							vectors.sort {it.y}
							h2 = (vectors.first().y).abs() + (vectors.last().y).abs()

							// code that may be better some how? from https://gist.github.com/nooone/8363982
//							BoundingBox boundingBox
//							shape.getVertex(0, vector)
//							vector = fixture.body.getWorldPoint(vector)
//							boundingBox = new BoundingBox(new Vector3(vector, 0), new Vector3(vector, 0))
//							for (int i = 1; i < shape.vertexCount; i++) {
//								shape.getVertex(i, vector)
//								boundingBox.ext(new Vector3(fixture.body.getWorldPoint(vector), 0))
//							}
//							w2 = boundingBox.width
//							h2 = boundingBox.height
						}
					}
				}

				if(w2 > 0f) {
					width = w2
				}

				if(h2 > 0f) {
					height = h2
				}


				float originX = width / 2f as float
				float originY = height / 2f as float
				float drawX = t.position.x - originX + tex.offsetX as float
				float drawY = t.position.y - originY + tex.offsetY as float

//				println("${Mapper.typeCom.get(entity).type} position: x: ${t.position.x}, y: ${t.position.y}")
				if(tex.region) {
					batch.draw(
							tex.region,
							t.flipX ? drawX + width as float : drawX,
							t.flipY ? drawY + height as float : drawY,
							originX,
							originY,
							t.flipX ? -width : width,
							t.flipY ? -height : height,
							pixelsToMeters(t.scale.x), pixelsToMeters(t.scale.y),
							t.rotation
					)
				} else {
					batch.draw(
							tex.texture,
							t.flipX ? drawX + width as float : drawX,
							t.flipY ? drawY + height as float : drawY,
							originX,
							originY,
							t.flipX ? -width : width,
							t.flipY ? -height : height,
							pixelsToMeters(t.scale.x), pixelsToMeters(t.scale.y)
					)
				}
			}
		}

		// Just some playing around, not sure where the white line comes from though, and why vertical in pong, and horizontal in worm???
		if (false) {
//			Interpose<Vector2> interposeSB = SteeringPresets.getInterpose(Mapper.sCom.get(levelFactory.enemyPaddle), Mapper.sCom.get(levelFactory.enemyScoringWall), Mapper.sCom.get(levelFactory.pingPong))
			ShapeRenderer shapeRenderer = new ShapeRenderer()
//			Vector2 point = new Vector2()

//			Vector2 posA = interposeSB.getAgentA().getPosition()
//			Vector2 posB = interposeSB.getAgentB().getPosition()

			// Draw line between agents
//			shapeRenderer.projectionMatrix = camera.combined
			shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
			shapeRenderer.color = Color.BLUE
			shapeRenderer.rectLine(screenSizeInMeters.x / 2 as float, 0, screenSizeInMeters.x / 2 as float, screenSizeInMeters.y, 50)
			shapeRenderer.end()


//			// Draw real target along the line between agents
//			point.set(posB).sub(posA).scl(interposeSB.getInterpositionRatio()).add(posA)
//			shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
//			shapeRenderer.setColor(1, 1, 0, 1)
//			shapeRenderer.circle(point.x, point.y, 4)
//			shapeRenderer.end()
//
//			// Draw estimated target
//			shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
//			shapeRenderer.setColor(1, 0, 0, 1)
//			shapeRenderer.circle(interposeSB.getInternalTargetPosition().x, interposeSB.getInternalTargetPosition().y, 4)
//			shapeRenderer.end()
		}


		batch.end()
		renderQueue.clear()
	}

	@Override
	void processEntity(Entity entity, float deltaTime) {
		if(entity) {
			renderQueue.add(entity)
		} else {
			println("entity not added due to being null: ${entity}")
		}
	}

	OrthographicCamera getCamera() {
		return cam
	}
}
