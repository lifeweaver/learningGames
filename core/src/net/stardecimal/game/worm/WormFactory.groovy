package net.stardecimal.game.worm

import com.badlogic.ashley.core.PooledEngine
import net.stardecimal.game.DefaultLevelFactory
import net.stardecimal.game.loader.SdAssetManager

class WormFactory implements DefaultLevelFactory {

	WormFactory(PooledEngine en, SdAssetManager assetManager) {
		init(en, assetManager)

		//Specific textures
		//worm
		//apples
		//etc
	}
}
