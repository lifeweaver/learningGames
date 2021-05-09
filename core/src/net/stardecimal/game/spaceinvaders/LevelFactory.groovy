package net.stardecimal.game.spaceinvaders

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.math.RandomXS128
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.BodyDef
import net.stardecimal.game.BodyFactory
import net.stardecimal.game.DefaultLevelFactory
import net.stardecimal.game.entity.components.BulletComponent
import net.stardecimal.game.entity.components.CollisionComponent
import net.stardecimal.game.entity.components.EnemyComponent
import net.stardecimal.game.entity.components.Mapper
import net.stardecimal.game.entity.components.PlayerComponent
import net.stardecimal.game.entity.components.SdBodyComponent
import net.stardecimal.game.entity.components.SoundEffectComponent
import net.stardecimal.game.entity.components.TextureComponent
import net.stardecimal.game.entity.components.TransformComponent
import net.stardecimal.game.entity.components.TypeComponent
import net.stardecimal.game.entity.systems.RenderingSystem
import net.stardecimal.game.loader.SdAssetManager

class LevelFactory implements DefaultLevelFactory {
	private TextureRegion enemy1Tex, enemy2Tex, enemy3Tex, enemy4Tex, playerTex, barrierTex, playerShotTex, enemyShotTex
	Sound enemy4Theme, enemyBlownUp, playerBlownUp, playerFiring, background
	RandomXS128 rand = new RandomXS128()
	Entity player

	LevelFactory(PooledEngine en, SdAssetManager assetManager) {
		init(en, assetManager)

		//Specific textures
		TextureAtlas atlas = assetManager.manager.get(SdAssetManager.gameImages)
		atlas.findRegion("space_invaders/")

		enemy1Tex = atlas.findRegion("space_invaders/enemy1")
		enemy2Tex = atlas.findRegion("space_invaders/enemy2")
		enemy3Tex = atlas.findRegion("space_invaders/enemy3")
		enemy4Tex = atlas.findRegion("space_invaders/enemy4")
		playerTex = atlas.findRegion("space_invaders/player")
		barrierTex = atlas.findRegion("space_invaders/barrier")
		playerShotTex = atlas.findRegion("space_invaders/playerShot")
		enemyShotTex = atlas.findRegion("space_invaders/enemyShot")

		enemy4Theme = assetManager.manager.get(SdAssetManager.enemy4Theme)
		enemyBlownUp = assetManager.manager.get(SdAssetManager.enemyBlownUp)
		playerBlownUp = assetManager.manager.get(SdAssetManager.playerBlownUp)
		playerFiring = assetManager.manager.get(SdAssetManager.playerFiring)
		background = assetManager.manager.get(SdAssetManager.background)


//		background bump that speeds up?
//		barriers that disappear in place shot? - maybe delete one particle from shot and one from wall?

		log.info("level factory initialized")
	}

	Entity createPlayer(OrthographicCamera cam) {
		Entity entity = engine.createEntity()
		SdBodyComponent sdBody = engine.createComponent(SdBodyComponent)
		TransformComponent position = engine.createComponent(TransformComponent)
		TextureComponent texture = engine.createComponent(TextureComponent)
		PlayerComponent playerCom = engine.createComponent(PlayerComponent)
		TypeComponent type = engine.createComponent(TypeComponent)
		Vector2 screenSize = RenderingSystem.getScreenSizeInMeters()

		playerCom.cam = cam
		sdBody.body = bodyFactory.makeBoxPolyBody(
				screenSize.x / RenderingSystem.PPM / 2 as float,
				3,
				2,
				0.75f,
				BodyFactory.STONE,
				BodyDef.BodyType.DynamicBody,
				true
		)

		texture.region = playerTex
		type.type = TypeComponent.TYPES.PLAYER
		sdBody.body.setUserData(entity)

		entity.add(sdBody)
		entity.add(position)
		entity.add(texture)
		entity.add(playerCom)
		entity.add(type)
		engine.addEntity(entity)
		player = entity

		return entity
	}

	void createEnemies() {
		Vector2 screenSize = RenderingSystem.getScreenSizeInMeters()
		Vector2 startPos = new Vector2(screenSize.x / RenderingSystem.PPM / 10 as float, (screenSize.y / RenderingSystem.PPM) - ((screenSize.y / RenderingSystem.PPM) / 10 * 2) as float)
		float origX = startPos.x

		[enemy3Tex, enemy2Tex, enemy2Tex, enemy1Tex, enemy1Tex].each {tex ->
			17.times {
				createEnemy(startPos, tex)
				startPos.x = startPos.x + 2 as float
			}
			startPos.x = origX
			startPos.y = startPos.y - 2 as float
		}
	}

	void createEnemy(Vector2 startPos, TextureRegion tex) {
		Entity entity = engine.createEntity()
		SdBodyComponent sdBody = engine.createComponent(SdBodyComponent)
		TransformComponent position = engine.createComponent(TransformComponent)
		TextureComponent texture = engine.createComponent(TextureComponent)
		TypeComponent type = engine.createComponent(TypeComponent)
		EnemyComponent eCom = engine.createComponent(EnemyComponent)

		sdBody.body = bodyFactory.makeBoxPolyBody(
				startPos.x,
				startPos.y,
				1.5,
				1,
				BodyFactory.STONE,
				BodyDef.BodyType.KinematicBody,
				true
		)

		texture.region = tex
		type.type = TypeComponent.TYPES.ENEMY
		sdBody.body.setUserData(entity)

		entity.add(eCom)
		entity.add(sdBody)
		entity.add(position)
		entity.add(texture)
		entity.add(type)
		engine.addEntity(entity)
	}

	void playerShoot() {
		//Get player position
		Vector2 startPos = Mapper.bCom.get(player).body.position
		createShot(startPos, true)
	}

	void createShot(Vector2 startPos, boolean isPlayer) {
		//Create shot
		Entity entity = engine.createEntity()
		SdBodyComponent sdBody = engine.createComponent(SdBodyComponent)
		TransformComponent position = engine.createComponent(TransformComponent)
		TextureComponent texture = engine.createComponent(TextureComponent)
		CollisionComponent colComp = engine.createComponent(CollisionComponent)
		TypeComponent type = engine.createComponent(TypeComponent)
		BulletComponent bul = engine.createComponent(BulletComponent)

		sdBody.body = bodyFactory.makeBoxPolyBody(
				startPos.x,
				startPos.y + (isPlayer ? 1 : -1) as float,
				0.25,
				0.5,
				BodyFactory.STONE,
				BodyDef.BodyType.DynamicBody,
				true
		)

		type.type = TypeComponent.TYPES.BULLET
		sdBody.body.bullet = true
		sdBody.body.setUserData(entity)
		bul.startPos = sdBody.body.position

		if(isPlayer) {
			texture.region = playerShotTex
			bul.yVel = 10
			bul.owner = BulletComponent.Owner.PLAYER

			SoundEffectComponent soundCom = engine.createComponent(SoundEffectComponent)
			soundCom.soundEffect = playerFiring
			soundCom.play()
			entity.add(soundCom)
		} else {
			texture.region = enemyShotTex
			bul.yVel = -10
			bul.owner = BulletComponent.Owner.ENEMY
	//		bul.particleEffect = makeParticleEffect(ParticleEffectManager.CONTRAIL, sdBody, 0, 0, true)
		}

		entity.add(bul)
		entity.add(sdBody)
		entity.add(position)
		entity.add(texture)
		entity.add(colComp)
		entity.add(type)
		engine.addEntity(entity)
	}

	@Override
	TiledMap generateBackground() {
		return null
	}

	@Override
	def createHud(SpriteBatch batch) {
		return null
	}
}
