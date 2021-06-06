package net.stardecimal.game.ikari_warriors

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.GL20
import net.stardecimal.game.GameScreen
import net.stardecimal.game.MyGames
import net.stardecimal.game.ikari_warriors.entity.systems.PlayerControlSystem

class IkariWarriorsScreen extends ScreenAdapter implements GameScreen {
	LevelFactory levelFactory

	//TODO:
	//everything
	/*
	 * Players start from bottom of the screen upwards, toward the village of ikari
	 *
	 * enemies:
	 *  - last boss
	 *  - soldier
	 *     - gun plus grenades - worth 200 points
	 *     - rocket
	 *     - flame thrower?
	 *     - can go under water
	 *       - only hurt by grenades then
	 *
	 *  - tank
	 *    - immune to enemy bullets
	 *    - limited fuel
	 *    - takes damage from explosions
	 *    - doesn't fire until player gets close?
	 *
	 *  - helicopter
	 *    - can fly over water
	 *    - two weapons
	 *       - spread gun
	 *       - cannon
	 *    - fires from longer range than tank
	 *
	 *  - other:
	 *     - trucks
	 *     - gun emplacements
	 *     - traps - rock faces?
	 *     - bases
	 *     - mines?
	 *       - hidden until close, starts beeping
	 *       - moving ones in water?
	 *
	 * movement obstacles:
	 *   - rocks
	 *   - barriers
	 *
	 * powerUps:
	 *  - Gun: restores machine gun ammo to maximum.
	 *  - Grenade: restores grenade ammo to maximum.
	 *  - Gas: restores all ammo to maximum and refuels the tank if the player is driving one.
	 *  - K: Kills all enemy troops on-screen, except ones hiding underwater.
	 *  - S: Gun and tank shots are sped up.
	 *  - L: Long-range shot. Bullets travel the entire length of the screen.
	 *  - F: Bullets turn red and can destroy trucks, gun emplacements and bases. Pierce shot. Bullets pass through rocks and other barriers without being blocked. Tank shots become rockets.
	 *  - B: Blast grenades. Grenades turn red and produce giant-sized explosions. All tank shots become blast grenades as well.
	 *
	 * Player:
	 *  - can commandeer enemy tanks and helicopters
	 *    - vehicle size is similar to player
	 *    - tank
	 *      - blowing up tank will kill player unless he gets out and clear of boom
	 *        - it makes some noise and looks different before it goes boom
	 *      - can run over solders
	 *      - uses grenade ammo
	 *      - makes sound when almost out of fuel
	 *    - helicopters -
	 *    - can swim
	 *      - slows player down
	 *    - bullets penetrate multiple enemies
	 *
	 *
	 * Player movement is separate from direction player facing
	 *  - player stays look at direction switch to, 8 ways
	 *  - I'm thinking wasd plus q and e for the rotation.
	 *  - Maybe left and right arrow for grenade and shooting respectively
	 *
	 * Players shoot from right hand, throw grenades from left, no shot is fired directly from in front of player
	 * If a player takes too long moving up the screen, initiate the 'call for fire' aka the red spot and a homing missile to that location
	 * Limited ammo - 99 bullets and 99 grenades
	 *
	 * 2 players
	 *
	 * bullets:
	 *  - short range, maybe half the screen height?
	 *  - don't collide with each other
	 *
	 *
	 * Basic road map:
	 *  - player movement
	 *  - player looking
	 *  - scrolling map
	 *  - shooting
	 *
	 */

	IkariWarriorsScreen(final MyGames game) {
//		init(game, LevelFactory.class, new RenderingConstants())
		init(game, LevelFactory.class)
		levelFactory = (LevelFactory) lvlFactory
		levelFactory.gameName = 'ikariWarriors'


		engine.addSystem(new PlayerControlSystem(levelFactory))
//		engine.addSystem(new CollisionSystem(parent, levelFactory))
//		engine.addSystem(new EnemySystem(levelFactory))

		levelFactory.createBoundaries(true, false)
		levelFactory.createPlayer(camera)
		levelFactory.playerLives = 3

//		parent.recorder = new GifRecorder(batch)
	}

	void resetWorld() {
		reset()
		levelFactory.playerScore = 0
		levelFactory.enemyScore = 0
		levelFactory.playerLives = 3
		levelFactory.createBoundaries(true, false)
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

			levelFactory.hud.setScore(levelFactory.playerScore)
			levelFactory.hud.setLives(levelFactory.playerLives)
		}

		// Gif Recorder support
		if(parent.recorder) {
			parent.recorder.update()
		}
	}

	@Override
	void hide() {
		super.hide()
	}

	@Override
	void show() {
		super.show()
		Gdx.input.setInputProcessor(parent.multiplexer)
	}

	@Override
	void dispose() {
		levelFactory.world.dispose()
		levelFactory.bodyFactory.dispose()
	}
}
