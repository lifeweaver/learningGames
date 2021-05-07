package net.stardecimal.game.loader

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.ParticleEffectLoader
import com.badlogic.gdx.assets.loaders.SkinLoader
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.ParticleEffect
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.ui.Skin

class SdAssetManager {
	final AssetManager manager = new AssetManager()

	// Textures
//	final static String gameImages = 'images/parent.atlas'
//	final static String loadingImages = 'images/loading.atlas'
//
//	final static String smokeEffect = "particles/smoke.pe"
//	final static String waterEffect = "particles/water.pe"
//	final static String fireEffect = "particles/fire.pe"

	final static String bounce = 'pong/bounce.wav'
	final static String paddleLeftLoss = 'pong/computerWhatAreYouDoing.wav'
	final static String paddleRightLoss = 'pong/computerSorry.wav'
	final static String skin = "skin/glassy-ui.json"

	final static String fruit = "worm/fruit.png"
	final static String worm = "worm/worm.png"

	final static String city = "missile_command/city.png"
	final static String defenderMissile = "missile_command/defenderMissile.png"
	final static String enemyMissileTrail = "missile_command/enemyMissileTrail.pe"
	final static String explosion = "missile_command/explosion.png"
	final static String explosionParticle = "missile_command/explosion.pe"
	final static String bomberPlane = "missile_command/bomberPlane.png"
	final static String satellite = "missile_command/satellite.png"
	final static String smartBomb = "missile_command/smartBomb.png"
	final static String crosshairs = "missile_command/crosshairs.png"
	final static String boom = "missile_command/boom.wav"
	final static String targeting_beep = "missile_command/targeting_beep.wav"

//	final static String playingSong = 'music/Rolemusic_-_pl4y1ng.mp3'
//

	void queueAddIndividualAssets() {
		manager.load(fruit, Texture)
		manager.load(worm, Texture)
		manager.load(city, Texture)
		manager.load(defenderMissile, Texture)
		manager.load(enemyMissileTrail, ParticleEffect)
		manager.load(explosion, Texture)
		manager.load(explosionParticle, ParticleEffect)
		manager.load(bomberPlane, Texture)
		manager.load(satellite, Texture)
		manager.load(smartBomb, Texture)
		manager.load(crosshairs, Texture)
	}

//	void queueAddImages() {
//		manager.load(gameImages, TextureAtlas.class)
//	}

//	void queueAddLoadingImages() {
//		manager.load(loadingImages, TextureAtlas.class)
//	}
//
//	void queueAddFonts() {
//
//	}

//	void queueAddParticleEffects() {
//		ParticleEffectLoader.ParticleEffectParameter pep = new ParticleEffectLoader.ParticleEffectParameter()
//		pep.atlasFile = "images/parent.atlas"
//		manager.load(smokeEffect, ParticleEffect.class, pep)
//		manager.load(waterEffect, ParticleEffect.class, pep)
//		manager.load(fireEffect, ParticleEffect.class, pep)
//	}

	void queueAddSounds() {
		manager.load(bounce, Sound)
		manager.load(paddleLeftLoss, Sound)
		manager.load(paddleRightLoss, Sound)
		manager.load(boom, Sound)
		manager.load(targeting_beep, Sound)
	}

	void queueAddMusic() {
//		manager.load(playingSong, Music.class)
	}

	void queueAddSkin(){
		SkinLoader.SkinParameter params = new SkinLoader.SkinParameter("skin/glassy-ui.atlas")
		manager.load(skin, Skin.class, params)
	}
}