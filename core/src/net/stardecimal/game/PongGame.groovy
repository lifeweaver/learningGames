package net.stardecimal.game

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3

class PongGame extends ApplicationAdapter {
	SpriteBatch batch
	OrthographicCamera camera
	Texture img, paddleImage, pingPongImage
	Rectangle paddleLeft, paddleRight, pingPong
	Vector3 touchPos
	float mousePreviousX, mousePreviousY
	int paddleLeftPoints, paddleRightPoints = 0
	final int INITIAL_SPEED = 350
	final int BOUNCE_INCREASE = 25
	float pingPongSpeedLimit = 2100
	float pingPongElapsed = 0.01f
	Vector2 pingPongLocation
	Vector2 pingPongVector
	Vector2 pingPongPaddleLeftIntersect
	Random random
	BitmapFont font

	@Override
	void create () {
		batch = new SpriteBatch()
		img = new Texture("badlogic.jpg")
		paddleImage = new Texture("paddle.png")
		pingPongImage = new Texture("pingPong.png")

		camera = new OrthographicCamera()
		camera.setToOrtho(false, 800, 480)

		pingPong = new Rectangle()
		pingPong.width = 10
		pingPong.height = 10
		pingPong.x = camera.viewportWidth / 2 - pingPong.width / 2
		pingPong.y = camera.viewportHeight / 2 - pingPong.height / 2

		paddleLeft = new Rectangle()
		paddleLeft.width = 10
		paddleLeft.height = 40
		paddleLeft.x = (float) 50 + paddleLeft.width / 2
		paddleLeft.y = camera.viewportHeight / 2 - paddleLeft.height / 2

		paddleRight = new Rectangle()
		paddleRight.width = 10
		paddleRight.height = 40
		paddleRight.x = (float) camera.viewportWidth - 50 - paddleRight.width / 2
		paddleRight.y = camera.viewportHeight / 2 - paddleRight.height / 2

		touchPos = new Vector3()
		pingPongLocation = new Vector2(pingPong.x, pingPong.y)
		pingPongPaddleLeftIntersect = new Vector2(paddleLeft.x, paddleLeft.y)
		pingPongVector = new Vector2(INITIAL_SPEED, 0)
		random = new Random()
		font = new BitmapFont()
	}

	@Override
	void render () {
		Gdx.gl.glClearColor(0,0,0, 1)
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

		camera.update()
		batch.setProjectionMatrix(camera.combined)

		batch.begin()
		font.draw(batch, paddleLeftPoints + " | " + paddleRightPoints,380, 450)
		batch.draw(paddleImage, paddleLeft.x, paddleLeft.y)
		batch.draw(paddleImage, paddleRight.x, paddleRight.y)
		batch.draw(pingPongImage, pingPong.x, pingPong.y)
		batch.end()


		touchPos.set(Gdx.input.x, Gdx.input.y, 0)
		camera.unproject(touchPos)

		if(Gdx.input.isKeyPressed(Input.Keys.NUM_1)) {
			if(pingPongVector.x > 0) {
				pingPongVector.x += 100
			} else {
				pingPongVector.x -= 100
			}
		}

		if(Gdx.input.isKeyPressed(Input.Keys.NUM_2)) {
			if(pingPongVector.x > 0) {
				pingPongVector.x -= 100
			} else {
				pingPongVector.x += 100
			}
		}

		paddleRightHandling()
		paddleLeftHandling()
		pingPongMovement()
		pingPongScoring()
	}

	void pingPongMovement() {
		pingPongLocation.with {
			x = pingPong.x
			y = pingPong.y
		}
		pingPongCollision()
		pingPongMotionChange()
	}

	private void pingPongCollision() {
		pingPongFloorCollision()
		pingPongCeilingCollision()
		pingPongPaddleRightCollision()
		pingPongPaddleLeftCollision()
	}

	private void pingPongPaddleLeftCollision() {
		if (pingPong.overlaps(paddleLeft) && pingPong.x < paddleLeft.x) {
			// get actual angle based on hit position
			pingPongVector.x -= BOUNCE_INCREASE
			pingPongVector.x = -pingPongVector.x

			// TODO: take into account the distance between the pingPong.y and the paddle.y
			def test = 100
			if (pingPong.y > paddleLeft.y) {
				pingPongVector.y += test
			} else {
				pingPongVector.y -= test
			}
		}
	}

	private void pingPongPaddleRightCollision() {
		if (pingPong.overlaps(paddleRight) && pingPong.x < paddleRight.x) {
			// get actual angle based on hit position
			pingPongVector.x += BOUNCE_INCREASE
			pingPongVector.x = -pingPongVector.x

			// TODO: take into account the distance between the pingPong.y and the paddle.y
			def test = 100
			if (pingPong.y > paddleRight.y) {
				pingPongVector.y += test
			} else {
				pingPongVector.y -= test
			}
		}
	}

	private void pingPongCeilingCollision() {
		if (pingPong.y >= camera.viewportHeight - pingPong.height) {
			pingPong.y = (float) camera.viewportHeight - pingPong.height
			pingPongVector.y -= 100
		}
	}

	private void pingPongFloorCollision() {
		if (pingPong.y <= 0) {
			pingPong.y = 0
			pingPongVector.y += 100
		}
	}

	private void pingPongMotionChange() {
		if (pingPongVector.x > 0 && pingPongVector.x > pingPongSpeedLimit) pingPongVector.x = pingPongSpeedLimit
		if (pingPongVector.x < 0 && pingPongVector.x < -pingPongSpeedLimit) pingPongVector.x = -pingPongSpeedLimit

		if (pingPongVector.x > 0) {
			pingPong.x = (float) pingPong.x + pingPongVector.x * pingPongElapsed
		} else {
			pingPong.x = (float) pingPong.x - -pingPongVector.x * pingPongElapsed
		}

		if (pingPongVector.y > 0) {
			pingPong.y = (float) pingPong.y + pingPongVector.y * pingPongElapsed
		} else {
			pingPong.y = (float) pingPong.y - -pingPongVector.y * pingPongElapsed
		}
	}

	private void pingPongScoring() {
		if (pingPong.x < 0) {
			// paddleRight scores point
			paddleRightPoints++
			resetPingPong()
		}

		if (pingPong.x > camera.viewportWidth - pingPong.width) {
			// paddleLeft scores point
			paddleLeftPoints++
			resetPingPong()
		}
	}

	private void paddleRightHandling() {
		if (!Gdx.input.isKeyPressed(Input.Keys.UP) && !Gdx.input.isKeyPressed(Input.Keys.DOWN) && (mousePreviousX != touchPos.x || mousePreviousY != touchPos.y)) {
			mousePreviousX = touchPos.x
			mousePreviousY = touchPos.y
			paddleRight.y = (float) touchPos.y - paddleRight.height / 2
		}

		if (Gdx.input.isKeyPressed(Input.Keys.UP)) paddleRight.y += 500 * Gdx.graphics.deltaTime
		if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) paddleRight.y -= 500 * Gdx.graphics.deltaTime

		if (paddleRight.y < 0) paddleRight.y = 0
		if (paddleRight.y > camera.viewportHeight - paddleLeft.height) paddleRight.y = (float) camera.viewportHeight - paddleRight.height
	}

	private void paddleLeftHandling() {
		if(!Gdx.input.isTouched()){
			if(pingPongLocation.y > paddleLeft.y + 10) {
				paddleLeft.y += 500 * Gdx.graphics.deltaTime
			} else if(pingPongLocation.y < paddleLeft.y - 10) {
				paddleLeft.y -= 500 * Gdx.graphics.deltaTime
			}
		}

		if (paddleLeft.y < 0) paddleLeft.y = 0
		if (paddleLeft.y > camera.viewportHeight - paddleLeft.height) paddleLeft.y = (float) camera.viewportHeight - paddleLeft.height
	}

	void resetPingPong() {
		System.out.println("Max pingpong speed: " + pingPongVector.x)
		pingPong.x = camera.viewportWidth / 2 - pingPong.width / 2
		pingPong.y = camera.viewportHeight / 2 - pingPong.height / 2
		pingPongVector.x = INITIAL_SPEED
		pingPongVector.y = 0
	}

	@Override
	void dispose () {
		img.dispose()
		paddleImage.dispose()
		pingPongImage.dispose()
		batch.dispose()
	}
}
