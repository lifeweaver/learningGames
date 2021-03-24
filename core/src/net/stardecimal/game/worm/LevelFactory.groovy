package net.stardecimal.game.worm

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef
import net.stardecimal.game.BodyFactory
import net.stardecimal.game.DFUtils
import net.stardecimal.game.DefaultLevelFactory
import net.stardecimal.game.entity.components.CollisionComponent
import net.stardecimal.game.entity.components.PlayerComponent
import net.stardecimal.game.entity.components.SdBodyComponent
import net.stardecimal.game.entity.components.StateComponent
import net.stardecimal.game.entity.components.SteeringComponent
import net.stardecimal.game.entity.components.TextureComponent
import net.stardecimal.game.entity.components.TransformComponent
import net.stardecimal.game.entity.components.TypeComponent
import net.stardecimal.game.entity.systems.RenderingSystem
import net.stardecimal.game.loader.SdAssetManager

class LevelFactory implements DefaultLevelFactory {
	private TextureRegion boundaryTex, wormTex
	private Texture fruitTex

	LevelFactory(PooledEngine en, SdAssetManager assetManager) {
		init(en, assetManager)

		//Specific textures
		boundaryTex = DFUtils.makeTextureRegion(RenderingSystem.getScreenSizeInMeters().x / RenderingSystem.PPM as float, 0.1f, '#ffffff')
		wormTex  = DFUtils.makeTextureRegion(1.5, 1.5, '#7CFC00')
		fruitTex = assetManager.manager.get(SdAssetManager.fruit)
	}


	Entity createPlayer(OrthographicCamera cam){
		Entity entity = engine.createEntity()
		SdBodyComponent sdBody = engine.createComponent(SdBodyComponent)
		TransformComponent position = engine.createComponent(TransformComponent)
		TextureComponent texture = engine.createComponent(TextureComponent)
		PlayerComponent player = engine.createComponent(PlayerComponent)
		CollisionComponent colComp = engine.createComponent(CollisionComponent)
		TypeComponent type = engine.createComponent(TypeComponent)
		StateComponent stateCom = engine.createComponent(StateComponent)
		SteeringComponent scom = engine.createComponent(SteeringComponent)
		Vector2 screenSize = RenderingSystem.getScreenSizeInMeters()

		player.cam = cam
		sdBody.body = bodyFactory.makeBoxPolyBody(screenSize.x / RenderingSystem.PPM / 2 as float, screenSize.y / RenderingSystem.PPM / 2 as float, 1.5, 1.5, BodyFactory.STONE, BodyDef.BodyType.DynamicBody, true)

		texture.region = wormTex
		type.type = TypeComponent.PLAYER
		stateCom.set(StateComponent.STATE_NORMAL)
		sdBody.body.setUserData(entity)
		sdBody.body.sleepingAllowed = false
		scom.body = sdBody.body

		entity.add(sdBody)
		entity.add(position)
		entity.add(texture)
		entity.add(player)
		entity.add(colComp)
		entity.add(type)
		entity.add(stateCom)
		entity.add(scom)

		engine.addEntity(entity)
		this.player = entity
		return entity
	}

	Entity createFruit() {
		Entity entity = engine.createEntity()
		SdBodyComponent sdBody = engine.createComponent(SdBodyComponent)
		TransformComponent position = engine.createComponent(TransformComponent)
		TextureComponent texture = engine.createComponent(TextureComponent)
		TypeComponent type = engine.createComponent(TypeComponent)
		Vector2 screenSize = RenderingSystem.getScreenSizeInMeters()

		sdBody.body = bodyFactory.makeCirclePolyBody(randomPos(0, screenSize.x) / RenderingSystem.PPM / 2 as float, randomPos(0, screenSize.y) / RenderingSystem.PPM / 2 as float,1.5f, BodyFactory.STONE, BodyDef.BodyType.DynamicBody,true)
		texture.region = new TextureRegion(fruitTex)

		type.type = TypeComponent.SCORE_WALL
		sdBody.body.setUserData(entity)

		entity.add(sdBody)
		entity.add(position)
		entity.add(texture)
		entity.add(type)

		engine.addEntity(entity)
		return entity
	}

	void createBoundaries() {
		Vector2 screenSize = RenderingSystem.getScreenSizeInMeters()
		float boundaryWidth = 0.1f

		//Floor
		createBoundary(new Vector2(screenSize.x, 0.1f), screenSize.x, boundaryWidth)

		//Ceiling
		createBoundary(new Vector2(screenSize.x, screenSize.y * 2 as float), screenSize.x, boundaryWidth)

		//Right wall
		createBoundary(new Vector2(screenSize.x * 2 as float, screenSize.y * 2 as float), boundaryWidth, screenSize.y)

		//Left wall
		createBoundary(new Vector2(0, 0), boundaryWidth, screenSize.y)
	}

	void createBoundary(Vector2 pos, float width, float height) {
		Entity entity = engine.createEntity()
		SdBodyComponent sdBody = engine.createComponent(SdBodyComponent)
		TransformComponent position = engine.createComponent(TransformComponent)
		TextureComponent texture = engine.createComponent(TextureComponent)
		TypeComponent type = engine.createComponent(TypeComponent)

		//Divide by the PPM then by 22 unless they are zero
		float x = pos.x != 0 ? pos.x / RenderingSystem.PPM / 2 as float : pos.x
		float y = pos.y != 0 ? pos.y / RenderingSystem.PPM / 2 as float : pos.y

		position.position.set(x, y, 0)
		texture.region = boundaryTex
		type.type = TypeComponent.SCENERY
		sdBody.body = bodyFactory.makeBoxPolyBody(x, y, width, height, BodyFactory.STONE, BodyDef.BodyType.StaticBody)

		entity.add(sdBody)
		entity.add(texture)
		entity.add(position)
		entity.add(type)

		sdBody.body.setUserData(entity)

		engine.addEntity(entity)
	}

	static float randomPos(float corner1, corner2) {
		Random rand = new Random()
		if(corner1 == corner2) {
			return corner1
		}
		float delta = corner2 - corner1 as float
		float offset = rand.nextFloat() * delta as float
		return corner1 + offset
	}
}
