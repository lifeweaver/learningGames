package net.stardecimal.game.tetris

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.math.RandomXS128
import com.badlogic.gdx.math.Vector2
import net.stardecimal.game.DefaultHud
import net.stardecimal.game.DefaultLevelFactory
import net.stardecimal.game.entity.components.CollisionComponent
import net.stardecimal.game.entity.components.Mapper
import net.stardecimal.game.entity.components.PlayerComponent
import net.stardecimal.game.entity.components.TextureComponent
import net.stardecimal.game.entity.components.TransformComponent
import net.stardecimal.game.entity.components.TypeComponent
import net.stardecimal.game.loader.SdAssetManager
import net.stardecimal.game.tetris.entity.components.ActiveComponent
import net.stardecimal.game.tetris.entity.components.BlockComponent

class LevelFactory implements DefaultLevelFactory {
	private TextureRegion iTex, jTex, lTex, oTex, sTex, tTex, zTex
	RandomXS128 rand = new RandomXS128()
	Entity player
	float fallingRateIncrease = 0f
	float speedUp = 0f
	List<int[]> grid
	static final ComponentMapper<BlockComponent> blockCom = ComponentMapper.getFor(BlockComponent.class)

	LevelFactory(PooledEngine en, SdAssetManager assetManager) {
		init(en, assetManager)

		//Specific textures
		TextureAtlas atlas = assetManager.manager.get(SdAssetManager.gameImages)
		atlas.findRegion("tetris/")

		iTex = atlas.findRegion("tetris/i")
		jTex = atlas.findRegion("tetris/j")
		lTex = atlas.findRegion("tetris/l")
		oTex = atlas.findRegion("tetris/o")
		sTex = atlas.findRegion("tetris/s")
		tTex = atlas.findRegion("tetris/t")
		zTex = atlas.findRegion("tetris/z")

		grid = generateCleanGrid()

		log.info("level factory initialized")
	}

	//Returns the filled vs not filled blocks for each shape in the provided rotation
	//Starting from the bottom left
	static List<int[]> getBlockTypeFilled(BlockComponent.BlockType blockType, float rotation=0) {
		List<int[]> filled = []
		float positiveRotation = Math.abs(rotation) % 360

		switch(blockType) {
			case BlockComponent.BlockType.I:
				filled = [[1], [1], [1], [1]]
				break

			case BlockComponent.BlockType.J:
				filled = [[1, 1, 1], [1, 0, 0]]
				break

			case BlockComponent.BlockType.L:
				filled = [[1, 1, 1], [0, 0, 1]]
				break

			case BlockComponent.BlockType.O:
				filled = [[1, 1], [1, 1]]
				break

			case BlockComponent.BlockType.S:
				filled = [[1, 1, 0], [0, 1, 1]]
				break

			case BlockComponent.BlockType.T:
				filled = [[1, 1, 1], [0, 1, 0]]
				break

			case BlockComponent.BlockType.Z:
				filled = [[0, 1, 1], [1, 1, 0]]
				break
		}

		if(positiveRotation == 90) {
			filled = rotateFilled((List<int[]>) filled, rotation > 0)
		}

		if(positiveRotation == 180) {
			filled = rotateFilled((List<int[]>) filled, rotation > 0)
			filled = rotateFilled(filled, rotation > 0)
		}

		if(positiveRotation == 270) {
			filled = rotateFilled(filled, rotation > 0)
			filled = rotateFilled(filled, rotation > 0)
			filled = rotateFilled(filled, rotation > 0)
		}

		return (List<int[]>) filled
	}

	static List<int[]> rotateFilled(List<int[]> filled, boolean isRight=true) {
		List<int[]> newFilled = []
		int height = filled.size()
		int width = filled.first().size()

		if(isRight) {
			width.times {w ->
				def newRow = []
				(height - 1).downto(0, {h ->
					newRow << filled[h.intValue()][w]
				})
				newFilled << (int[]) newRow
			}
		}

		if(!isRight) {
			(width - 1).downto(0, {w ->
				def newRow = []
				height.times {h ->
					newRow << filled[h][w.intValue()]
				}
				newFilled << (int[]) newRow
			})
		}


		return newFilled
	}

	static List<int[]> generateCleanGrid() {
		int gridHeight = 18
		int gridWidth = 10
		List<int[]> thisGrid = []
		gridHeight.times {height ->
			def row = []
			gridWidth.times{width ->
				row << 0
			}
			thisGrid << (int[]) row
		}
		return thisGrid
	}

	static List<int[]> copyGrid(List<int[]> sourceGrid) {
		List<int[]> newGrid = []
		sourceGrid.each {height ->
			def row = []
			height.each {width ->
				row << width
			}
			newGrid << (int[]) row
		}

		return newGrid
	}

	boolean isValidMove(Entity entity) {
		try {
			int newSlots = 4 // Always 4, every piece only has 4 duh
			int currentSlots = grid.flatten().count(1) as int
			List<int[]> tempGrid = calculateGrid(entity, copyGrid(grid))

			return tempGrid.flatten().count(1) as int - currentSlots == newSlots
		} catch(ArrayIndexOutOfBoundsException ignored) {
			return false
		}
	}

	int adjustedWidth(Entity entity, boolean max=true) {
		TextureComponent texCom = Mapper.texCom.get(entity)
		TransformComponent transCom = Mapper.transCom.get(entity)
		BlockComponent.BlockType blockType = blockCom.get(entity).blockType
		List<int[]> filled = getBlockTypeFilled(blockType, transCom.rotation)
		Vector2 bottomLeft = determineBottomLeft(transCom, texCom)

		if(max) {
			return bottomLeft.x + (filled.first().size())
		} else {
			return bottomLeft.x
		}
	}

	List<int[]> calculateGrid(Entity entity, List<int[]> thisGrid) {
		TextureComponent texCom = Mapper.texCom.get(entity)
		TransformComponent transCom = Mapper.transCom.get(entity)
		BlockComponent.BlockType blockType = blockCom.get(entity).blockType
		List<int[]> filled = getBlockTypeFilled(blockType, transCom.rotation)
		Vector2 bottomLeft = determineBottomLeft(transCom, texCom)
		int startX = bottomLeft.x as int
		int startY = bottomLeft.y as int

		int yDifference = 0
		int xDifference = 0
		filled.each {row ->
			xDifference = 0
			row.each {column ->
				//Only fill if it's not already filled
				if(thisGrid[startY + yDifference][startX + xDifference] == 0) {
					thisGrid[startY + yDifference][startX + xDifference] = column
				}
				xDifference += 1
			}
			yDifference += 1
		}

		return thisGrid
	}

	static void displayGrid(List<int[]> thisGrid) {
		thisGrid.size().times {row ->
			thisGrid.reverse()[row - 1].each {column ->
				print(" ${column}")
			}
			print("\n")
		}
	}

	void spawnRandomBlock() {
		List blockTypes = BlockComponent.BlockType.values().toList()
		Collections.shuffle(blockTypes)
		createBlock(blockTypes.first())

//		createBlock(BlockComponent.BlockType.I)
//		createBlock(BlockComponent.BlockType.J)
//		createBlock(BlockComponent.BlockType.L)
//		createBlock(BlockComponent.BlockType.O)
//		createBlock(BlockComponent.BlockType.S)
//		createBlock(BlockComponent.BlockType.T)
//		createBlock(BlockComponent.BlockType.Z)
	}

	void collision(Entity entity) {
		entity.remove(PlayerComponent)
		entity.remove(CollisionComponent)
		entity.remove(ActiveComponent)
		Mapper.typeCom.get(entity).type = TypeComponent.TYPES.OTHER

		//Update main grid
		grid = calculateGrid(entity, grid)
		displayGrid(grid)

		spawnRandomBlock()
	}

	static Vector2 determineBottomLeft(TransformComponent transCom, TextureComponent texCom) {
		float x = 0
		float y = 0

		//Change value used for width/height to account for the rotation
		int testValue = Math.abs(transCom.rotation) as int

		if(testValue == 90 || testValue == 270) {
			x = transCom.position.x - texCom.region.regionHeight / 2 as float
			y = transCom.position.y - texCom.region.regionWidth / 2 as float
		} else {
			x = transCom.position.x - texCom.region.regionWidth / 2 as float
			y = transCom.position.y - texCom.region.regionHeight / 2 as float
		}

		return new Vector2(Math.round(x), Math.round(y))
	}

	void createBlock(BlockComponent.BlockType blockType) {
		//Reset controls on death
		controller.reset()

		Entity entity = engine.createEntity()
		TransformComponent position = engine.createComponent(TransformComponent)
		TextureComponent texture = engine.createComponent(TextureComponent)
		PlayerComponent playerCom = engine.createComponent(PlayerComponent)
		TypeComponent type = engine.createComponent(TypeComponent)
		CollisionComponent colCom = engine.createComponent(CollisionComponent)
		ActiveComponent activeCom = engine.createComponent(ActiveComponent)
		BlockComponent blockCom = engine.createComponent(BlockComponent)

		blockCom.blockType = blockType
		updateBlockDisplay(blockCom.blockType, texture)
		type.type = TypeComponent.TYPES.PLAYER
		position.position.x = grid.first().size() / 2
		position.position.y = grid.size() - 2


		entity.add(blockCom)
		entity.add(activeCom)
		entity.add(colCom)
		entity.add(position)
		entity.add(texture)
		entity.add(playerCom)
		entity.add(type)
		engine.addEntity(entity)
		player = entity
	}

	void updateBlockDisplay(BlockComponent.BlockType blockType, TextureComponent texture) {
		texture.initialOffsetX = 0.5
		texture.offsetX = texture.initialOffsetX

		switch(blockType) {
			case BlockComponent.BlockType.I:
				texture.region = iTex
				break

			case BlockComponent.BlockType.J:
				texture.region = jTex
				break

			case BlockComponent.BlockType.L:
				texture.region = lTex
				break

			case BlockComponent.BlockType.O:
				texture.region = oTex
				texture.initialOffsetX = 0
				texture.offsetX = texture.initialOffsetX
				break

			case BlockComponent.BlockType.S:
				texture.region = sTex
				break

			case BlockComponent.BlockType.T:
				texture.region = tTex

				break

			case BlockComponent.BlockType.Z:
				texture.region = zTex
				break
		}
	}

	@Override
	TiledMap generateBackground() {
		return null
	}

	@Override
	def createHud(SpriteBatch batch) {
		hud = new DefaultHud(batch)
		return hud
	}
}
