package net.stardecimal.game.missilecommand.entity.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Vector3
import net.stardecimal.game.KeyboardController
import net.stardecimal.game.entity.components.PlayerComponent
import net.stardecimal.game.missilecommand.LevelFactory
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class PlayerControlSystem extends IteratingSystem {
	KeyboardController controller
	LevelFactory levelFactory
	private static final Logger log = LoggerFactory.getLogger(PlayerControlSystem)
	long lastClick = System.currentTimeMillis()
	OrthographicCamera camera

	@SuppressWarnings("unchecked")
	PlayerControlSystem(KeyboardController keyCon, LevelFactory lvlFactory, OrthographicCamera cam) {
		super(Family.all(PlayerComponent.class).get())
		controller = keyCon
		levelFactory = lvlFactory
		camera = cam
	}
	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		if(controller.isMouse1Down && System.currentTimeMillis() - lastClick > 200 ) {
//			log.debug("lastClick: ${lastClick}, mouseLocation.x: ${controller.mouseLocation.x / RenderingSystem.PPM}, mouseLocation.y: ${controller.mouseLocation.y / RenderingSystem.PPM}")
			lastClick = System.currentTimeMillis()
			Vector3 gameCoords = camera.unproject(new Vector3(controller.mouseLocation.x, controller.mouseLocation.y, 0))
			levelFactory.launchDefenderMissile(gameCoords.x, gameCoords.y)
		}
	}
}