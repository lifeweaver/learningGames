package net.stardecimal.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera

class MainMenuScreen extends ScreenAdapter {
	final PongGame game
	OrthographicCamera camera

	MainMenuScreen(final PongGame game) {
		this.game = game

		camera = new OrthographicCamera()
		camera.setToOrtho(false, 800, 480)
	}

	@Override
	void render(float delta) {
		Gdx.gl.glClearColor(0,0,0, 1)
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

		camera.update()
		game.batch.setProjectionMatrix(camera.combined)

		game.batch.begin()
		game.font.draw(game.batch, "Welcome to Ping Pong!!!", 280, 300)
		game.font.draw(game.batch, "Press anywhere to begin!!!", 280, 250)
		game.batch.end()

		if (Gdx.input.isTouched()) {
			game.setScreen(new GameScreen(game))
			dispose()
		}
	}
}
