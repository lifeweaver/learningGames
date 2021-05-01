package net.stardecimal.game.breakout

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.utils.Array
import net.stardecimal.game.BodyFactory
import net.stardecimal.game.DFUtils
import net.stardecimal.game.DefaultLevelFactory
import net.stardecimal.game.breakout.entity.components.PowerUpComponent
import net.stardecimal.game.entity.components.BulletComponent
import net.stardecimal.game.entity.components.CollisionComponent
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
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class LevelFactory implements DefaultLevelFactory {
	TextureRegion pingPongTex, defaultBoxTex, explodeBoxTex, doubleBoxTex, powerUpBoxTex, paddleTex
	Entity pingPong
	private static final Logger log = LoggerFactory.getLogger(LevelFactory)
	List<Entity> boxes = []
	Random rand = new Random()

	LevelFactory(PooledEngine en, SdAssetManager assetManager) {
		init(en, assetManager)

		//Specific textures
		pingPongTex = DFUtils.makeTextureRegion(1, 1, '#ffffff')
		paddleTex = DFUtils.makeTextureRegion(1, 1, '#ffffff')

		defaultBoxTex = DFUtils.makeTextureRegion(2, 1, '#ffffff')
		explodeBoxTex = DFUtils.makeTextureRegion(2, 1, '#ff0000')
		doubleBoxTex = DFUtils.makeTextureRegion(2, 1, '#808080')
		powerUpBoxTex = DFUtils.makeTextureRegion(2, 1, '#D4AF37')
		log.info("level factory initialized")
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
		sdBody.body = bodyFactory.makeBoxPolyBody(screenSize.x / RenderingSystem.PPM / 2 as float, 0 / RenderingSystem.PPM + 2 as float, 2, 0.5f, BodyFactory.STONE, BodyDef.BodyType.DynamicBody, true)

		texture.region = paddleTex
		type.type = TypeComponent.TYPES.PLAYER
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

	Entity createPingPong() {
		Array<Body> bodies = new Array<>()
		world.getBodies(bodies)

		def pingPongs = engine.entities.findAll {Entity entity ->
			Mapper.typeCom.get(entity).type == TypeComponent.TYPES.BULLET && Mapper.bulletCom.get(entity) && !Mapper.bulletCom.get(entity).isDead
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
		PowerUpComponent powerUp = engine.createComponent(PowerUpComponent)
		Vector2 screenSize = RenderingSystem.getScreenSizeInMeters()
		SteeringComponent scom = engine.createComponent(SteeringComponent)

		Vector2 pingPongPosition = new Vector2(screenSize.x / RenderingSystem.PPM / 2 as float, screenSize.y / RenderingSystem.PPM / 2 / 2 as float)

		sdBody.body = bodyFactory.makeCirclePolyBody(pingPongPosition.x, pingPongPosition.y,0.4f, BodyFactory.PING_PONG, BodyDef.BodyType.DynamicBody,true)
		sdBody.body.setBullet(true) // increase physics computation to limit body travelling through other objects
		texture.region = pingPongTex

		type.type = TypeComponent.TYPES.BULLET
		sdBody.body.setUserData(entity)
		bul.xVel = 0
		bul.yVel = 15f
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
		entity.add(powerUp)

		engine.addEntity(entity)
		pingPong = entity
		return entity
	}

	void createBoxes() {
		Vector2 screenSize = RenderingSystem.getScreenSizeInMeters()
		screenSize.x = screenSize.x / RenderingSystem.PPM
		screenSize.y = screenSize.y / RenderingSystem.PPM
		for (double row = (screenSize.y / 2).round(); row < screenSize.y; row++) {
			for (double col = 1; col < screenSize.x; col=col + 2) {
				boxes << createBox(col, row)
			}
		}
	}

	Entity createBox(double x, double y) {
		Entity entity = engine.createEntity()
		SdBodyComponent sdBody = engine.createComponent(SdBodyComponent)
		TextureComponent texture = engine.createComponent(TextureComponent)
		TypeComponent type = engine.createComponent(TypeComponent)
		TransformComponent position = engine.createComponent(TransformComponent)

		sdBody.width = 2
		sdBody.height = 1
		sdBody.body = bodyFactory.makeBoxPolyBody(
				x as float,
				y as float,
				sdBody.width,
				sdBody.height,
				BodyFactory.STONE,
				BodyDef.BodyType.StaticBody,
				true
		)
		sdBody.body.setUserData(entity)

		int typeChance = rand.nextInt(100)
		if(typeChance <= 5) {
			texture.region = explodeBoxTex
			type.type = TypeComponent.TYPES.ENEMY_EXPLODE
		} else if(typeChance <= 20) {
			texture.region = doubleBoxTex
			type.type = TypeComponent.TYPES.ENEMY_DOUBLE
		} else if(typeChance >= 95) {
			texture.region = powerUpBoxTex
			type.type = TypeComponent.TYPES.POWER_UP
		} else {
			texture.region = defaultBoxTex
			type.type = TypeComponent.TYPES.ENEMY
		}

		entity.add(sdBody)
		entity.add(texture)
		entity.add(position)
		entity.add(type)
		engine.addEntity(entity)

		return entity
	}

	TiledMap generateBackground() {
		return null
	}
}
