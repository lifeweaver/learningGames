package net.stardecimal.game.tetris.entity.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.utils.Array
import net.stardecimal.game.entity.components.Mapper
import net.stardecimal.game.entity.components.TransformComponent
import net.stardecimal.game.entity.components.TypeComponent
import net.stardecimal.game.tetris.LevelFactory

class LineClearingSystem extends IteratingSystem {
	private LevelFactory levelFactory
	private Array<Entity> blocksQueue

	@SuppressWarnings('uncheck')
	LineClearingSystem(LevelFactory lvlFactory) {
		super(Family.all(TransformComponent.class).get())
		this.blocksQueue = new Array<Entity>()
		levelFactory = lvlFactory
		priority = 21
	}

	@Override
	void update(float deltaTime) {
		super.update(deltaTime)

		//Check for full line
		def rowsToDelete = new ArrayList<Integer>()
		levelFactory.grid.eachWithIndex {row, index ->

			//Delete every block in a row if the entire row is filled
			if(row.count(1) == levelFactory.gridWidth) {
				rowsToDelete << index
			}
		}

		//Find blocks in that row
		List<Entity> entitiesToMove = []
		rowsToDelete.each { rowToDelete ->
			List<Entity> entitiesToRemove = []
			blocksQueue.each { Entity entity ->
				float y = Mapper.transCom.get(entity)?.position?.y
				float yPosToDelete = rowToDelete + levelFactory.gridBottom - 0.5

				if(y == yPosToDelete) {
					entitiesToRemove << entity
				}


				if(y > yPosToDelete) {
					entitiesToMove << entity
				}
			}

			entitiesToRemove.each { Entity entity ->
				entitiesToMove.removeAll(entity)
				updateGrid(entity)
				engine.removeEntity(entity)
			}
		}

		//Find all blocks above each line cleared and move them down 1 block starting from the top
		//We can only do this after we delete all the existing, otherwise we'll delete the blocks we just moved
		entitiesToMove.sort {Mapper.transCom.get(it).position.y }.each { Entity entity ->
			TransformComponent transCom = Mapper.transCom.get(entity)
			if(transCom) {
				updateGrid(entity, 0)
				transCom.position.y--
				updateGrid(entity, 1)
			}
		}

		blocksQueue.clear()
	}

	void updateGrid(Entity entity, int operation=0) {
		TransformComponent transCom = Mapper.transCom.get(entity)
		int startX = transCom.position.x as int
		int startY = transCom.position.y - (levelFactory.gridBottom - 0.5) as int
		levelFactory.grid[startY][startX] = operation
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		if(!blocksQueue.contains(entity) && Mapper.typeCom.get(entity).type == TypeComponent.TYPES.OTHER) {
			blocksQueue.add(entity)
		}
	}

}
