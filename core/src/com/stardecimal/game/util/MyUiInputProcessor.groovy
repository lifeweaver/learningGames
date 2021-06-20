package com.stardecimal.game.util

import com.badlogic.gdx.Input
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.math.Vector2
import com.stardecimal.game.GameJamGame

class MyUiInputProcessor implements InputProcessor {

	boolean esc
	boolean isMouse1Down, isMouse2Down, isMouse3Down
	boolean isDragged
	Vector2 mouseLocation = new Vector2()
	GameJamGame parent

	MyUiInputProcessor(GameJamGame game) {
		parent = game
	}

	@Override
	boolean keyDown(int keycode) {
		boolean keyProcessed = false
		switch (keycode) {
			case Input.Keys.ESCAPE:
				esc = true
				if(parent.state == GameJamGame.STATE.RUNNING) {
					parent.changeScreen(GameJamGame.PAUSE)
				}
				keyProcessed = true
		}

		return keyProcessed
	}

	@Override
	boolean keyUp(int keycode) {
		boolean keyProcessed = false
		switch (keycode) {
			case Input.Keys.ESCAPE:
				esc = false
				keyProcessed = true
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
}
