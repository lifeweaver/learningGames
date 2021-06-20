package com.stardecimal.game.util

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.ParticleEffectLoader
import com.badlogic.gdx.assets.loaders.SkinLoader
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.ParticleEffect
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.badlogic.gdx.scenes.scene2d.ui.Skin

class SdAssetManager {
	final AssetManager manager = new AssetManager()

	SdAssetManager() {
		//Allow loading of TiledMaps
		manager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()))
	}

	// Textures
	final static String gameImages = 'atlas.atlas'
//	final static String loadingImages = 'images/loading.atlas'
	final static String skin = "skin/glassy-ui.json"

	final static String gameMap = "maps/map.tmx"

	void queueAddIndividualAssets() {
		manager.load(gameMap, TiledMap)
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

	}

	void queueAddMusic() {
//		manager.load(playingSong, Music.class)
	}

	void queueAddSkin(){
		SkinLoader.SkinParameter params = new SkinLoader.SkinParameter("skin/glassy-ui.atlas")
		manager.load(skin, Skin.class, params)
	}
}