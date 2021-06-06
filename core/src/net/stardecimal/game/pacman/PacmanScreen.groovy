package net.stardecimal.game.pacman

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.GL20
import net.stardecimal.game.RenderingConstants
import net.stardecimal.game.GameScreen
import net.stardecimal.game.MyGames
import net.stardecimal.game.pacman.entity.systems.CollisionSystem
import net.stardecimal.game.pacman.entity.systems.EnemySystem
import net.stardecimal.game.pacman.entity.systems.PlayerControlSystem

class PacmanScreen extends ScreenAdapter implements GameScreen {
	LevelFactory levelFactory
	int maxScore = 0

	//TODO:
	//make tunnel wrap
	//make inky's special seeking mode behavior
	//make clyde's special seeking mode behavior
	//fruit
	//Add dying animation
	//stop sounds if paused or game ends.
	//make ghosts slow on turns?
	//update speed of ghosts in different modes


	PacmanScreen(final MyGames game) {
		init(game, LevelFactory.class, new RenderingConstants())
		levelFactory = (LevelFactory) lvlFactory
		levelFactory.gameName = 'pacman'

		engine.addSystem(new PlayerControlSystem(levelFactory))
		engine.addSystem(new CollisionSystem(parent, levelFactory))
		engine.addSystem(new EnemySystem(levelFactory))

		levelFactory.createPlayer(camera)
		levelFactory.playerLives = 3

//		parent.recorder = new GifRecorder(batch)
	}

	void resetWorld() {
		reset()
		levelFactory.playerScore = 0
		levelFactory.enemyScore = 0
		levelFactory.playerLives = 3
		levelFactory.createPlayer(camera)
	}


	@Override
	void render(float delta) {
		if(parent.state == MyGames.STATE.RUNNING) {
			Gdx.gl.glClearColor(0,0,0, 1)
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
			engine.update(delta)

			//Move to end game screen once all lives used up
			if(levelFactory.playerLives == -1) {
				parent.changeScreen(parent.ENDGAME)
			}

			if((levelFactory.playerScore - maxScore) / 1000 > 1) {
				maxScore = levelFactory.playerScore
				levelFactory.playerLives++
			}

			levelFactory.hud.setScore(levelFactory.playerScore)
			levelFactory.hud.setLives(levelFactory.playerLives)

//			//Draw nodes and paths for debugging
//			PacGraph blinkyGraph = engine.getSystem(EnemySystem).blinkyGraph
//			levelFactory.shapeRenderer.setProjectionMatrix(camera.combined)
//
//			blinkyGraph.pacPaths.each {
//				//draw from
//				levelFactory.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
//				levelFactory.shapeRenderer.setColor(0.8f, 0.88f, 0.95f, 1) //blue
//				Vector2 fromGamePos = levelFactory.gamePosition(it.fromNode.x, it.fromNode.y)
//				levelFactory.shapeRenderer.circle(fromGamePos.x, fromGamePos.y, 0.1)
//				levelFactory.shapeRenderer.end()
//
//				levelFactory.shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
//				levelFactory.shapeRenderer.setColor(1, 1, 1, 1)
//				levelFactory.shapeRenderer.circle(fromGamePos.x, fromGamePos.y, 0.1)
//				levelFactory.shapeRenderer.end()
//
//				//draw to
//				levelFactory.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
//				levelFactory.shapeRenderer.setColor(0.57f, 0.76f, 0.48f, 1) //green
//				Vector2 toGamePos = levelFactory.gamePosition(it.toNode.x, it.toNode.y)
//				levelFactory.shapeRenderer.circle(toGamePos.x, toGamePos.y, 0.1)
//				levelFactory.shapeRenderer.end()
//
//				levelFactory.shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
//				levelFactory.shapeRenderer.setColor(1, 1, 1, 1)
//				levelFactory.shapeRenderer.circle(toGamePos.x, toGamePos.y, 0.1)
//				levelFactory.shapeRenderer.end()
//
//
//				//draw line between
//				levelFactory.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
//				levelFactory.shapeRenderer.setColor(1, 1, 1, 1)
//				levelFactory.shapeRenderer.rectLine(fromGamePos.x, fromGamePos.y, toGamePos.x, toGamePos.y, 0.1)
//				levelFactory.shapeRenderer.end()
//			}
		}

		// Gif Recorder support
		if(parent.recorder) {
			parent.recorder.update()
		}
	}

	@Override
	void show() {
		Gdx.input.setInputProcessor(parent.multiplexer)
	}

	@Override
	void dispose() {
		levelFactory.world.dispose()
		levelFactory.bodyFactory.dispose()
	}
}
