package net.stardecimal.game

class DefaultRenderingConstants {
	static final float PPM = 16
	static final float MPP = 1 / PPM // get the ration for converting pixels to metres

	static final int WORLD_PIXEL_WIDTH = 640
	static final int WORLD_PIXEL_HEIGHT = 480
	static final float WORLD_WIDTH = WORLD_PIXEL_WIDTH / PPM
	static final float WORLD_HEIGHT = WORLD_PIXEL_HEIGHT / PPM
}
