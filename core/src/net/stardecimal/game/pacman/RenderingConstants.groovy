package net.stardecimal.game.pacman

import net.stardecimal.game.DefaultRenderingConstants

class RenderingConstants extends DefaultRenderingConstants {
	static final float PPM = 32
	static final float MPP = 1 / PPM // get the ration for converting pixels to metres
	static final int WORLD_PIXEL_WIDTH = 224
	static final int WORLD_PIXEL_HEIGHT = 288
	static final float WORLD_WIDTH = WORLD_PIXEL_WIDTH / PPM
	static final float WORLD_HEIGHT = WORLD_PIXEL_HEIGHT / PPM
}
