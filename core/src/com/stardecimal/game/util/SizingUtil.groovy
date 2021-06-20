package com.stardecimal.game.util

class SizingUtil {
	static float WORLD_TO_BOX = 0.1f
	static float BOX_TO_WORLD = 100f

	static float convertToBox(float x){
		return x * WORLD_TO_BOX
	}

	static float convertToWorld(float x){
		return x * BOX_TO_WORLD
	}

}
