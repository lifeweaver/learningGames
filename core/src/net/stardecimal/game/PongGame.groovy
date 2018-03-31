package net.stardecimal.game

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector3

class PongGame extends ApplicationAdapter {
	SpriteBatch batch
	OrthographicCamera camera
	Texture img, paddleImage, pingPongImage
	Rectangle paddleLeft, paddleRight
	PingPong pingPong
	Vector3 touchPos
	float mousePreviousX, mousePreviousY
	int paddleLeftPoints, paddleRightPoints = 0
	Random random
	BitmapFont font
	Sound bounce
	Sound paddleLeftLoss
	Sound paddleRightLoss

	@Override
	void create () {
		batch = new SpriteBatch()
		img = new Texture("badlogic.jpg")
		paddleImage = new Texture("paddle.png")
		pingPongImage = new Texture("pingPong.png")

		camera = new OrthographicCamera()
		camera.setToOrtho(false, 800, 480)

		pingPong = new PingPong()
		pingPong.with {
			x = camera.viewportWidth / 2 - pingPong.width / 2
			y = camera.viewportHeight / 2 - pingPong.height / 2
		}

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
		random = new Random()
		font = new BitmapFont()
		bounce = Gdx.audio.newSound(Gdx.files.internal("bounce.wav"))
		paddleLeftLoss = Gdx.audio.newSound(Gdx.files.internal("computerWhatAreYouDoing.wav"))
		paddleRightLoss = Gdx.audio.newSound(Gdx.files.internal("computerSorry.wav"))
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
		controls()

		paddleRightMovement()
		paddleLeftMovement()
		collision()
		pingPong.applyMovement()
		scoring()
	}

	private void controls() {
		if (Gdx.input.isKeyPressed(Input.Keys.NUM_1)) {
			if (pingPong.direction.x > 0) {
				pingPong.direction.x += 100
			} else {
				pingPong.direction.x -= 100
			}
		}

		if (Gdx.input.isKeyPressed(Input.Keys.NUM_2)) {
			if (pingPong.direction.x > 0) {
				pingPong.direction.x -= 100
			} else {
				pingPong.direction.x += 100
			}
		}
	}

	private void collision() {
		floorCollision()
		ceilingCollision()
		paddleRightCollision()
		paddleLeftCollision()
	}

	private void paddleLeftCollision() {
		if (pingPong.overlaps(paddleLeft) && pingPong.x >= paddleLeft.x) {
			pingPong.direction.x = -pingPong.direction.x
			pingPong.direction.x -= pingPong.BOUNCE_INCREASE

			// TODO: get actual angle based on hit position
			// TODO: take into account the distance between the pingPong.y and the paddle.y
			def test = 100
			if (pingPong.y > paddleLeft.y + paddleLeft.width / 2) {
				pingPong.direction.y += test
			} else {
				pingPong.direction.y -= test
			}
			bounce.play()
		}
	}

	private void paddleRightCollision() {
		if (pingPong.overlaps(paddleRight) && pingPong.x <= paddleRight.x) {
			pingPong.direction.x = -pingPong.direction.x
			pingPong.direction.x += pingPong.BOUNCE_INCREASE

			// TODO: get actual angle based on hit position
			// TODO: take into account the distance between the pingPong.y and the paddle.y
			def test = 100
			if (pingPong.y > paddleRight.y + paddleRight.width / 2) {
				pingPong.direction.y += test
			} else {
				pingPong.direction.y -= test
			}
			bounce.play()
		}
	}

	private void ceilingCollision() {
		if (pingPong.y >= camera.viewportHeight - pingPong.height) {
			pingPong.y = (float) camera.viewportHeight - pingPong.height
			pingPong.direction.y -= 100
		}
	}

	private void floorCollision() {
		if (pingPong.y <= 0) {
			pingPong.y = 0
			pingPong.direction.y += 100
		}
	}

	private void scoring() {
		if (pingPong.x < 0) {
			paddleRightPoints++
			paddleLeftLoss.play()
			newRound()
		}

		if (pingPong.x > camera.viewportWidth - pingPong.width) {
			paddleLeftPoints++
			paddleRightLoss.play()
			newRound()
		}
	}

	private void paddleRightMovement() {
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

	private void paddleLeftMovement() {
		if(!Gdx.input.isTouched()){
			if(pingPong.location.y > paddleLeft.y + 10) {
				paddleLeft.y += 500 * Gdx.graphics.deltaTime
			} else if(pingPong.location.y < paddleLeft.y) {
				paddleLeft.y -= 500 * Gdx.graphics.deltaTime
			}
		}

		if (paddleLeft.y < 0) paddleLeft.y = 0
		if (paddleLeft.y > camera.viewportHeight - paddleLeft.height) paddleLeft.y = (float) camera.viewportHeight - paddleLeft.height
	}

	void newRound() {
		System.out.println("Max pingpong speed: " + pingPong.direction.x)
		pingPong.x = camera.viewportWidth / 2 - pingPong.width / 2
		pingPong.y = camera.viewportHeight / 2 - pingPong.height / 2
		pingPong.direction.x = pingPong.INITIAL_SPEED
		pingPong.direction.y = 0
		paddleLeft.y = camera.viewportHeight / 2 - paddleLeft.height / 2
		paddleRight.y = camera.viewportHeight / 2 - paddleRight.height / 2
	}

	@Override
	void dispose () {
		img.dispose()
		paddleImage.dispose()
		pingPongImage.dispose()
		batch.dispose()
		paddleLeftLoss.dispose()
		paddleRightLoss.dispose()
		bounce.dispose()
	}
}
