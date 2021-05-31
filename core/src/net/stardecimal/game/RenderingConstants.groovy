package net.stardecimal.game

class RenderingConstants {
	static float PPM = 16
	static float MPP = 1 / PPM // get the ration for converting pixels to metres

	static int WORLD_PIXEL_WIDTH = 640
	static int WORLD_PIXEL_HEIGHT = 480
	static float WORLD_WIDTH = WORLD_PIXEL_WIDTH / PPM
	static float WORLD_HEIGHT = WORLD_PIXEL_HEIGHT / PPM
}
