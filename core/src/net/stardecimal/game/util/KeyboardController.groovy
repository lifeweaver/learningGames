package net.stardecimal.game.util

import com.badlogic.gdx.Input
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.math.Vector2

class KeyboardController implements InputProcessor {

	boolean left, right, up, down, spacbar
	boolean isMouse1Down, isMouse2Down, isMouse3Down
	boolean isDragged
	Vector2 mouseLocation = new Vector2()

	@Override
	boolean keyDown(int keycode) {
		boolean keyProcessed = false
		switch (keycode) {
			case Input.Keys.LEFT:
				left = true
				keyProcessed = true
				break
			case Input.Keys.RIGHT:
				right = true
				keyProcessed = true
				break
			case Input.Keys.UP:
				up = true
				keyProcessed = true
				break
			case Input.Keys.DOWN:
				down = true
				keyProcessed = true
				break
			case Input.Keys.SPACE:
				spacbar = true
				keyProcessed = true
				break
		}

		return keyProcessed
	}

	@Override
	boolean keyUp(int keycode) {
		boolean keyProcessed = false
		switch (keycode) {
			case Input.Keys.LEFT:
				left = false
				keyProcessed = true
				break
			case Input.Keys.RIGHT:
				right = false
				keyProcessed = true
				break
			case Input.Keys.UP:
				up = false
				keyProcessed = true
				break
			case Input.Keys.DOWN:
				down = false
				keyProcessed = true
				break
			case Input.Keys.SPACE:
				spacbar = false
				keyProcessed = true
				break
		}

		return keyProcessed
	}

	@Override
	boolean keyTyped(char character) {
		return false
	}

	@Override
	boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if(button == 0) {
			isMouse1Down = true
		} else  if(button == 1) {
			isMouse2Down = true
		} else if(button == 2) {
			isMouse3Down = true
		}
		mouseLocation.x = screenX
		mouseLocation.y = screenY
		return false
	}

	@Override
	boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if(button == 0) {
			isMouse1Down = false
		} else  if(button == 1) {
			isMouse2Down = false
		} else if(button == 2) {
			isMouse3Down = false
		}
		mouseLocation.x = screenX
		mouseLocation.y = screenY
		return false
	}

	@Override
	boolean touchDragged(int screenX, int screenY, int pointer) {
		isDragged = true
		mouseLocation.x = screenX
		mouseLocation.y = screenY
		return false
	}

	@Override
	boolean mouseMoved(int screenX, int screenY) {
		mouseLocation.x = screenX
		mouseLocation.y = screenY
		return false
	}

	@Override
	boolean scrolled(float amountX, float amountY) {
		return false
	}

	void reset() {
		left = false
		right = false
		up = false
		down = false
		isMouse1Down = false
		isMouse2Down = false
		isMouse3Down = false
		spacbar = false
	}
}