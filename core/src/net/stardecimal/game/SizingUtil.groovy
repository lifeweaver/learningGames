package net.stardecimal.game

class SizingUtil {
	float WORLD_TO_BOX = 0.1f
	float BOX_TO_WORLD = 100f

	static float convertToBox(float x){
		return x * 0.1f
	}

	static float convertToWorld(float x){
		return x * 100f
	}

}
