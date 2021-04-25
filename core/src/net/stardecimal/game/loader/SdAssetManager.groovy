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

//	final static String playingSong = 'music/Rolemusic_-_pl4y1ng.mp3'
//

	void queueAddIndividualAssets() {
		manager.load(fruit, Texture)
		manager.load(worm, Texture)
		manager.load(city, Texture)
		manager.load(defenderMissile, Texture)
		manager.load(enemyMissileTrail, ParticleEffect)
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
		manager.load(bounce, Sound.class)
		manager.load(paddleLeftLoss, Sound.class)
		manager.load(paddleRightLoss, Sound.class)
	}

	void queueAddMusic() {
//		manager.load(playingSong, Music.class)
	}

	void queueAddSkin(){
		SkinLoader.SkinParameter params = new SkinLoader.SkinParameter("skin/glassy-ui.atlas")
		manager.load(skin, Skin.class, params)
	}
}