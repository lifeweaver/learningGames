package net.stardecimal.game

import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.maps.tiled.TiledMap
import net.stardecimal.game.loader.SdAssetManager
import net.stardecimal.game.util.DefaultHud
import net.stardecimal.game.util.DefaultLevelFactory

class LevelFactory implements DefaultLevelFactory {

	LevelFactory(PooledEngine en, SdAssetManager assetManager) {
		init(en, assetManager)

		//Specific textures
//		paddleTex = DFUtils.makeTextureRegion(1, 1, '#ffffff')
//		pingPongTex = DFUtils.makeTextureRegion(1, 1, '#ffffff')
//		boundaryTex = DFUtils.makeTextureRegion(RenderingSystem.getScreenSizeInMeters().x / RenderingSystem.PPM as float, 0.1f, '#ffffff')
//		enemyScoreWallTex = DFUtils.makeTextureRegion(0.1, 1, '#000000')
	}


	TiledMap generateBackground() {
		return null
	}

	@Override
	def createHud(SpriteBatch batch) {
		hud = new DefaultHud(batch)
		return hud
	}
}
