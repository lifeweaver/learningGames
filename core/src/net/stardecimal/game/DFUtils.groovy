package net.stardecimal.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Cursor
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3

class DFUtils {
	private static String[] fnames
	private static String[] lnames
	private static Pixmap pmap

	/**
	 *  Converts HSV values to RGBA
	 * @param hue The hue input value
	 * @param saturation The saturation of the colour
	 * @param value the value of the colour
	 * @param alpha the alpha to output with RGB
	 * @return The RGBA value
	 */
	static Color hsvToRgba(float hue, float saturation, float value, float alpha) {

		int h = (int)(hue * 6)
		float f = hue * 6 - h as float
		float p = value * (1 - saturation) as float
		float q = value * (1 - f * saturation) as float
		float t = value * (1 - (1 - f) * saturation) as float

		switch (h) {
			case 0: return new Color(value, t, p,alpha)
			case 1: return new Color(q, value, p,alpha)
			case 2: return new Color(p, value, t,alpha)
			case 3: return new Color(p, q, value,alpha)
			case 4: return new Color(t, p, value,alpha)
			case 5: return new Color(value, p, q,alpha)
			default: throw new RuntimeException("Something went wrong when converting from HSV to RGB. Input was " + hue + ", " + saturation + ", " + value)
		}
	}

	/** converts RGB 0-1 to hex string e.g. FFFFFF
	 * @param r red value 0-1
	 * @param g green value 0-1
	 * @param b blue value 0-1
	 * @return RGB in HEX
	 */
	static String rgbToString(float r, float g, float b) {
		String rs = Integer.toHexString((int)(r * 256))
		String gs = Integer.toHexString((int)(g * 256))
		String bs = Integer.toHexString((int)(b * 256))
		return rs + gs + bs
	}

	/**
	 * Generates a random name using 2 text files in the assets folder
	 * @return random name
	 */
	static String generateRandomName(){
		String name = ""
		if(fnames == null){
			FileHandle fnfile = Gdx.files.internal("fname.txt")
			fnames = fnfile.readString().split("\n")

			FileHandle lnfile = Gdx.files.internal("lname.txt")
			lnames = lnfile.readString().split("\n")
		}
		int fni = (int)(Math.random() * fnames.length)
		name = fnames[fni].trim()

		int lni = (int)(Math.random() * lnames.length)
		name += "_"+lnames[lni].trim()

		return name
	}

	static Texture makeTexture(int width, int height, String hex){
		if(hex.length() == 6 ){
			hex+="FF"
		}
		return makeTexture(width,height,Color.valueOf(hex))
	}

	static TextureRegion makeTextureRegion(int width, int height, String hex){
		if(hex.length() == 6 ){
			hex+="FF"
		}
		return makeTextureRegion(width,height,Color.valueOf(hex))
	}

	static TextureRegion makeTextureRegion(float width, float height, String hex) {
		int fval = (int)width
		int gval = (int)height
		return makeTextureRegion(fval,gval,hex)
	}

	static TextureRegion makeTextureRegion(int width, int height, Color col){
		TextureRegion tex = new TextureRegion(makeTexture(width,height,col))
		return tex
	}

	static Texture makeTexture(int width, int height, Color col){
		Texture tex
		tex = new Texture(makePixMap(width,height,col))
		disposePmap()
		return tex
	}

	private static Pixmap makePixMap(int width, int height, Color fill){
		pmap = new Pixmap(width, height,Pixmap.Format.RGBA8888)
		pmap.setColor(fill)
		pmap.fill()
		return pmap
	}

	private static void disposePmap(){
		pmap.dispose()
	}

	static TextureRegion[] spriteSheetToFrames(TextureRegion region, int FRAME_COLS, int FRAME_ROWS){
		// split texture region
		TextureRegion[][] tmp = region.split(region.regionWidth / FRAME_COLS as int, region.regionHeight / FRAME_ROWS as int)

		// compact 2d array to 1d array
		TextureRegion[] frames = new TextureRegion[FRAME_COLS * FRAME_ROWS]
		int index = 0
		for (int i = 0; i < FRAME_ROWS; i++) {
			for (int j = 0; j < FRAME_COLS; j++) {
				frames[index++] = tmp[i][j]
			}
		}

		return frames
	}

	//Use for going up?
	static float vectorToAngle (Vector2 vector) {
		return (float)Math.atan2(-vector.x, vector.y)
	}

	//Use for going down? vectorToAngle didn't work for the bomber plane's missile, I'm  not sure why, but this does.
	//And if I used this for aiming the defender missiles, then the angle is wrong.
	static float vectorToAngle2 (Vector2 vector) {
		return (float)Math.atan2(-vector.y, vector.x)
	}

	static Vector2 angleToVector (Vector2 outVector, float angle) {
		outVector.x = -(float)Math.sin(angle)
		outVector.y = (float)Math.cos(angle)
		return outVector
	}

	static Vector2 aimTo(Vector2 shooter, Vector2 target){
		Vector2 aim = new Vector2()
		float velx = target.x - shooter.x as float // get distance from shooter to target on x plain
		float vely = target.y - shooter.y as float // get distance from shooter to target on y plain
		float length = (float) Math.sqrt(velx * velx + vely * vely) // get distance to target direct
		if (length != 0) {
			aim.x = velx / length  // get required x velocity to aim at target
			aim.y = vely / length  // get required y velocity to aim at target
		}
		return aim;
	}


	/** Takes Vector 3 as argument here for mouse location(unproject etc)
	 * @param shooter Vector 2 for shooter position
	 * @param target Vector 3 for target location
	 * @return
	 */
	static Vector2 aimTo(Vector2 shooter, Vector3 target){
		return aimTo(shooter, new Vector2(target.x,target.y))
	}


	//Method I found in particle park
	static Pixmap textureRegionToPixmap(TextureRegion textureRegion) {
		Texture texture = textureRegion.getTexture()
		if (!texture.getTextureData().isPrepared()) {
			texture.getTextureData().prepare()
		}

		Pixmap pixmap = texture.getTextureData().consumePixmap()
		Pixmap returnValue = new Pixmap(textureRegion.getRegionWidth(), textureRegion.getRegionHeight(), Pixmap.Format.RGBA8888)
		returnValue.setBlending(Pixmap.Blending.None)

		for(int x = 0; x < textureRegion.getRegionWidth(); ++x) {
			for(int y = 0; y < textureRegion.getRegionHeight(); ++y) {
				int colorInt = pixmap.getPixel(textureRegion.getRegionX() + x, textureRegion.getRegionY() + y)
				returnValue.drawPixel(x, y, colorInt)
			}
		}

		pixmap.dispose();
		return returnValue;
	}

	//Method I found in particle park
	static Cursor textureRegionToCursor(TextureRegion textureRegion, int xHotspot, int yHotspot) {
		return Gdx.graphics.newCursor(textureRegionToPixmap(textureRegion), xHotspot, yHotspot)
	}
}