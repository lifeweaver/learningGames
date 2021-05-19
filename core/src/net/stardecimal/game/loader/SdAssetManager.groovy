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
	final static String gameImages = 'atlas.atlas'
//	final static String loadingImages = 'images/loading.atlas'
//
//	final static String smokeEffect = "particles/smoke.pe"
//	final static String waterEffect = "particles/water.pe"
//	final static String fireEffect = "particles/fire.pe"

	final static String bounce = 'input/pong/bounce.wav'
	final static String paddleLeftLoss = 'input/pong/computerWhatAreYouDoing.wav'
	final static String paddleRightLoss = 'input/pong/computerSorry.wav'
	final static String skin = "skin/glassy-ui.json"

	final static String enemyMissileTrail = "input/missile_command/enemyMissileTrail.pe"
	final static String flames = "input/asteroids/flames.pe"
	final static String shield = "input/asteroids/shield.pe"
	final static String explosionParticle = "input/missile_command/explosion.pe"
	final static String boom = "input/missile_command/boom.wav"
	final static String targeting_beep = "input/missile_command/targeting_beep.wav"

	final static String enemy4Theme = "input/space_invaders/enemy4Theme.wav"
	final static String background = "input/space_invaders/background.wav"
	final static String enemyBlownUp = "input/space_invaders/enemyBlownUp.wav"
	final static String playerBlownUp = "input/space_invaders/playerBlownUp.wav"
	final static String playerFiring = "input/space_invaders/playerFiring.wav"

//	final static String playingSong = 'music/Rolemusic_-_pl4y1ng.mp3'
//

	void queueAddIndividualAssets() {
		manager.load(enemyMissileTrail, ParticleEffect)
		manager.load(explosionParticle, ParticleEffect)
		manager.load(flames, ParticleEffect)
		manager.load(shield, ParticleEffect)
	}

	void queueAddImages() {
		manager.load(gameImages, TextureAtlas.class)
	}

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

		manager.load(enemy4Theme, Sound)
		manager.load(background, Sound)
		manager.load(enemyBlownUp, Sound)
		manager.load(playerBlownUp, Sound)
		manager.load(playerFiring, Sound)
	}

	void queueAddMusic() {
//		manager.load(playingSong, Music.class)
	}

	void queueAddSkin(){
		SkinLoader.SkinParameter params = new SkinLoader.SkinParameter("skin/glassy-ui.atlas")
		manager.load(skin, Skin.class, params)
	}
}