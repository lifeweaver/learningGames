package net.stardecimal.game


import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.World
import com.badlogic.gdx.utils.Array
import net.stardecimal.game.entity.components.BulletComponent
import net.stardecimal.game.entity.components.CollisionComponent
import net.stardecimal.game.entity.components.EnemyComponent
import net.stardecimal.game.entity.components.Mapper
import net.stardecimal.game.entity.components.PlayerComponent
import net.stardecimal.game.entity.components.SdBodyComponent
import net.stardecimal.game.entity.components.StateComponent
import net.stardecimal.game.entity.components.SteeringComponent
import net.stardecimal.game.entity.components.TextureComponent
import net.stardecimal.game.entity.components.TransformComponent
import net.stardecimal.game.entity.components.TypeComponent
import net.stardecimal.game.entity.systems.RenderingSystem
import net.stardecimal.game.loader.SdAssetManager

class PongLevelFactory {
	private BodyFactory bodyFactory
	public World world
	private PooledEngine engine
	private TextureRegion boundaryTex, paddleTex, pingPongTex, enemyScoreWallTex
	Entity player
	Entity enemyScoringWall
	Entity enemyPaddle
	Entity pingPong

	PongLevelFactory(PooledEngine en, SdAssetManager assetManager) {
		engine = en
		paddleTex = DFUtils.makeTextureRegion(1, 1, '#ffffff')
		pingPongTex = DFUtils.makeTextureRegion(1, 1, '#ffffff')
		boundaryTex = DFUtils.makeTextureRegion(RenderingSystem.getScreenSizeInMeters().x / RenderingSystem.PPM as float, 0.1f, '#ffffff')
		enemyScoreWallTex = DFUtils.makeTextureRegion(0.1, 1, '#000000')

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

	void createFloor(){
		Entity entity = engine.createEntity()
		SdBodyComponent sdBody = engine.createComponent(SdBodyComponent)
		TransformComponent position = engine.createComponent(TransformComponent)
		TextureComponent texture = engine.createComponent(TextureComponent)
		TypeComponent type = engine.createComponent(TypeComponent)
		Vector2 screenSize = RenderingSystem.getScreenSizeInMeters()

		position.position.set(screenSize.x / 2 as float, 0, 0)
		texture.region = boundaryTex
//		texture.offsetY = -0.4f
		type.type = TypeComponent.SCENERY
		sdBody.body = bodyFactory.makeBoxPolyBody(screenSize.x / RenderingSystem.PPM / 2 as float, 0.1f, screenSize.x / RenderingSystem.PPM as float, 0.1f, BodyFactory.STONE, BodyDef.BodyType.StaticBody)

		entity.add(sdBody)
		entity.add(texture)
		entity.add(position)
		entity.add(type)

		sdBody.body.setUserData(entity)

		engine.addEntity(entity)
	}

	void createCeiling(){
		Entity entity = engine.createEntity()
		SdBodyComponent sdBody = engine.createComponent(SdBodyComponent)
		TransformComponent position = engine.createComponent(TransformComponent)
		TextureComponent texture = engine.createComponent(TextureComponent)
		TypeComponent type = engine.createComponent(TypeComponent)
		Vector2 screenSize = RenderingSystem.getScreenSizeInMeters()

		position.position.set(screenSize.x / 2 as float, screenSize.y, 0)
		texture.region = boundaryTex
//		texture.offsetY = -0.4f
		type.type = TypeComponent.SCENERY
		sdBody.body = bodyFactory.makeBoxPolyBody(screenSize.x / RenderingSystem.PPM / 2 as float, screenSize.y / RenderingSystem.PPM - 0.2 as float, screenSize.x, 0.1f, BodyFactory.STONE, BodyDef.BodyType.StaticBody)

		entity.add(sdBody)
		entity.add(texture)
		entity.add(position)
		entity.add(type)

		sdBody.body.setUserData(entity)

		engine.addEntity(entity)
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
		sdBody.body = bodyFactory.makeBoxPolyBody(screenSize.x / RenderingSystem.PPM - 2 as float, screenSize.y / RenderingSystem.PPM / 2 as float, 0.5f, 2, BodyFactory.STONE, BodyDef.BodyType.DynamicBody, true)

		// set object position (x,y,z) z used to define draw order 0 first drawn
		position.position.set(screenSize.x - 2 as float, screenSize.y / 2 as float,0)
		texture.region = paddleTex
//		texture.offsetY = 0.5f
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
		println('player created')
		return entity
	}

	Entity createEnemy(){
		Entity entity = engine.createEntity()
		SdBodyComponent sdBody = engine.createComponent(SdBodyComponent)
		TransformComponent position = engine.createComponent(TransformComponent)
		TextureComponent texture = engine.createComponent(TextureComponent)
		EnemyComponent enemy = engine.createComponent(EnemyComponent)
		TypeComponent type = engine.createComponent(TypeComponent)
		CollisionComponent colComp = engine.createComponent(CollisionComponent)
		Vector2 screenSize = RenderingSystem.getScreenSizeInMeters()
		SteeringComponent scom = engine.createComponent(SteeringComponent)
		StateComponent stateCom = engine.createComponent(StateComponent)

		sdBody.body = bodyFactory.makeBoxPolyBody(2, screenSize.y / RenderingSystem.PPM / 2 as float, 0.5, 2, BodyFactory.STONE, BodyDef.BodyType.DynamicBody, true)
		position.position.set(2, screenSize.y / RenderingSystem.PPM / 2 as float,0)
		texture.region = paddleTex
		enemy.xPosCenter = 2
		type.type = TypeComponent.ENEMY
		stateCom.set(StateComponent.STATE_NORMAL)
		sdBody.body.setUserData(entity)
		scom.body = sdBody.body

//		scom.steeringBehavior = SteeringPresets.getWander(scom)
//		scom.currentMode = SteeringComponent.SteeringState.WANDER

//		SteeringComponent bulletScom = ((Entity) engine.getEntities().find {it.getComponent(TypeComponent).type == TypeComponent.BULLET}).getComponent(SteeringComponent)
//		scom.steeringBehavior = SteeringPresets.getArrive(scom, bulletScom)
//		scom.currentMode = SteeringComponent.SteeringState.ARRIVE

		entity.add(sdBody)
		entity.add(position)
		entity.add(texture)
		entity.add(colComp)
		entity.add(type)
		entity.add(enemy)
		entity.add(stateCom)
		entity.add(scom)

		engine.addEntity(entity)
		enemyPaddle = entity
		println('enemy created')
		return entity
	}

	void createEnemyScoringWall() {
		Entity entity = engine.createEntity()
		SdBodyComponent sdBody = engine.createComponent(SdBodyComponent)
		TransformComponent position = engine.createComponent(TransformComponent)
		TextureComponent texture = engine.createComponent(TextureComponent)
		TypeComponent type = engine.createComponent(TypeComponent)
		StateComponent stateCom = engine.createComponent(StateComponent)
		SteeringComponent scom = engine.createComponent(SteeringComponent)
		Vector2 screenSize = RenderingSystem.getScreenSizeInMeters()

		position.position.set(0, screenSize.y / RenderingSystem.PPM / 2 as float, 0)
		texture.region = enemyScoreWallTex
//		texture.offsetY = -0.4f
		type.type = TypeComponent.SCORE_WALL
		sdBody.body = bodyFactory.makeBoxPolyBody(0, screenSize.y / RenderingSystem.PPM / 2 as float, 0.1f, screenSize.y, BodyFactory.STONE, BodyDef.BodyType.StaticBody)
		scom.body = sdBody.body

		entity.add(sdBody)
		entity.add(texture)
		entity.add(position)
		entity.add(type)
		entity.add(stateCom)
		entity.add(scom)

		sdBody.body.setUserData(entity)

		engine.addEntity(entity)
		this.enemyScoringWall = entity
	}

	Entity createPingPong() {
		Array<Body> bodies = new Array<>()
		world.getBodies(bodies)

		def pingPongs = engine.entities.findAll {Entity entity ->
			Mapper.typeCom.get(entity).type == TypeComponent.BULLET && Mapper.bulletCom.get(entity) && !Mapper.bulletCom.get(entity).isDead
		}
		if(pingPongs.size() > 0) {
			return null
		}

		Entity entity = engine.createEntity()
		SdBodyComponent sdBody = engine.createComponent(SdBodyComponent)
		TransformComponent position = engine.createComponent(TransformComponent)
		TextureComponent texture = engine.createComponent(TextureComponent)
		StateComponent stateCom = engine.createComponent(StateComponent)
		TypeComponent type = engine.createComponent(TypeComponent)
		CollisionComponent colComp = engine.createComponent(CollisionComponent)
		BulletComponent bul = engine.createComponent(BulletComponent)
		Vector2 screenSize = RenderingSystem.getScreenSizeInMeters()
		SteeringComponent scom = engine.createComponent(SteeringComponent)

		sdBody.body = bodyFactory.makeCirclePolyBody(screenSize.x / RenderingSystem.PPM / 2 as float, screenSize.y / RenderingSystem.PPM / 2 as float,0.2f, BodyFactory.PING_PONG, BodyDef.BodyType.DynamicBody,true)
		sdBody.body.setBullet(true) // increase physics computation to limit body travelling through other objects
		position.position.set(screenSize.x / RenderingSystem.PPM / 2 as float, screenSize.y / RenderingSystem.PPM / 2 as float,0)
		texture.region = pingPongTex

		type.type = TypeComponent.BULLET
		sdBody.body.setUserData(entity)
		bul.xVel = 15f
		bul.yVel = 0
		bul.owner = BulletComponent.Owner.SCENERY
		scom.body = sdBody.body

		entity.add(bul)
		entity.add(colComp)
		entity.add(sdBody)
		entity.add(position)
		entity.add(texture)
		entity.add(stateCom)
		entity.add(type)
		entity.add(scom)


		engine.addEntity(entity)
		pingPong = entity
		println("pingPong created - position: ${position.position}")
		return entity
	}
}
