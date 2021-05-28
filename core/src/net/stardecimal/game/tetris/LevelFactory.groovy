package net.stardecimal.game.tetris

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.PooledEngine
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.math.RandomXS128
import com.badlogic.gdx.math.Vector2
import net.stardecimal.game.DFUtils
import net.stardecimal.game.DefaultHud
import net.stardecimal.game.DefaultLevelFactory
import net.stardecimal.game.entity.components.CollisionComponent
import net.stardecimal.game.entity.components.Mapper
import net.stardecimal.game.entity.components.PlayerComponent
import net.stardecimal.game.entity.components.TextureComponent
import net.stardecimal.game.entity.components.TransformComponent
import net.stardecimal.game.entity.components.TypeComponent
import net.stardecimal.game.entity.systems.RenderingSystem
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
	static final ComponentMapper<BlockComponent> blockMapper = ComponentMapper.getFor(BlockComponent.class)
	static final ComponentMapper<ActiveComponent> activeMapper = ComponentMapper.getFor(ActiveComponent.class)
	static int gridHeight = 18
	static int gridWidth = 10
	int gridTop = RenderingSystem.screenSizeInMeters.y / RenderingSystem.PPM as int
	int gridBottom = gridTop - gridHeight
	BlockComponent.BlockType nextBlock
	Entity previewBlock

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
		nextBlock = randomBlock()
		updatePreviewGrid()

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
		BlockComponent.BlockType blockType = blockMapper.get(entity).type
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
		BlockComponent.BlockType blockType = blockMapper.get(entity).type
		List<int[]> filled = getBlockTypeFilled(blockType, transCom.rotation)
		Vector2 bottomLeft = determineBottomLeft(transCom, texCom)
		int startX = bottomLeft.x as int
		int startY = bottomLeft.y - (gridBottom - 1) as int

		int yDifference = 0
		int xDifference = 0
		filled.each {row ->
			xDifference = 0
			row.each {column ->
				//Kick out if x is negative
				if(startX + xDifference < 0) {
					throw new ArrayIndexOutOfBoundsException()
				}

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
		println("")
	}

	static BlockComponent.BlockType randomBlock() {
		List blockTypes = BlockComponent.BlockType.values().toList()
		Collections.shuffle(blockTypes)
		return blockTypes.first()
	}

	void updatePreviewGrid() {
		if(previewBlock) {
			engine.removeEntity(previewBlock)
		}
		previewBlock = createBlock(nextBlock, new Vector2(14, 27), null, TypeComponent.TYPES.SCENERY)
	}

	void spawnRandomBlock() {
		createBlock(nextBlock)
		nextBlock = randomBlock()
		updatePreviewGrid()

//		createBlock(BlockComponent.BlockType.I)
//		createBlock(BlockComponent.BlockType.J)
//		createBlock(BlockComponent.BlockType.L)
//		createBlock(BlockComponent.BlockType.O)
//		createBlock(BlockComponent.BlockType.S)
//		createBlock(BlockComponent.BlockType.T)
//		createBlock(BlockComponent.BlockType.Z)

		//End game if the spawned block wasn't a valid move.
		if(!isValidMove(player)) {
			playerLives--
		}
	}

	void collision(Entity entity) {
		entity.remove(PlayerComponent)
		entity.remove(ActiveComponent)

		//Update main grid
		grid = calculateGrid(entity, grid)
		displayGrid(grid)

		//Replace block with single blocks
		replaceBlockWithSingleBlocks(entity)
		player = null
		engine.removeEntity(entity)

		spawnRandomBlock()
	}

	void replaceBlockWithSingleBlocks(Entity entity) {
		BlockComponent blockComponent = blockMapper.get(entity)
		TransformComponent transCom = Mapper.transCom.get(entity)
		TextureComponent texCom = Mapper.texCom.get(entity)
		List<int[]> filled = getBlockTypeFilled(blockComponent.type, transCom.rotation)
		Vector2 bottomLeft = determineBottomLeft(transCom, texCom)
		int startX = bottomLeft.x as int
		int startY = bottomLeft.y - (gridBottom - 1) as int

		int yDifference = 0
		int xDifference = 0
		filled.each {row ->
			xDifference = 0
			row.each {column ->
				//Only add a block there is it's a 1
				if(column) {
					float x = startX + xDifference + 0.5
					float y = startY + yDifference + gridBottom - 0.5
					createBlock(blockComponent.type, new Vector2(x, y), blockComponent.color)
				}
				xDifference += 1
			}
			yDifference += 1
		}

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

	Entity createBlock(BlockComponent.BlockType blockType, Vector2 startPos=new Vector2(gridWidth / 2, gridTop - 3), Color color=null, int type=0) {
		Entity entity = engine.createEntity()
		TransformComponent position = engine.createComponent(TransformComponent)
		TextureComponent texture = engine.createComponent(TextureComponent)
		TypeComponent typeCom = engine.createComponent(TypeComponent)

		//If there is a block type, that means it's a new block, otherwise it's a 1 by 1 block
		if(!color) {
			PlayerComponent playerCom = engine.createComponent(PlayerComponent)
			ActiveComponent activeCom = engine.createComponent(ActiveComponent)
			BlockComponent blockCom = engine.createComponent(BlockComponent)
			blockCom.type = blockType
			updateBlockDisplay(blockCom, texture)
			typeCom.type = type ?: TypeComponent.TYPES.PLAYER

			if(typeCom.type == TypeComponent.TYPES.PLAYER) {
				//Reset controls on death
				controller.reset()

				entity.add(blockCom)
				entity.add(activeCom)
				entity.add(playerCom)
				player = entity
			}
		} else {
			texture.region = DFUtils.makeTextureRegion(1, 1, color)
			typeCom.type = TypeComponent.TYPES.OTHER
		}

		position.position.x = startPos.x
		position.position.y = startPos.y

		entity.add(position)
		entity.add(texture)
		entity.add(typeCom)
		engine.addEntity(entity)
		return entity
	}

	void updateBlockDisplay(BlockComponent blockComponent, TextureComponent texture) {
		texture.initialOffsetX = 0.5
		texture.offsetX = texture.initialOffsetX

		switch(blockComponent.type) {
			case BlockComponent.BlockType.I:
				texture.region = iTex
				blockComponent.color = Color.valueOf('30C7EDFF')
				break

			case BlockComponent.BlockType.J:
				texture.region = jTex
				blockComponent.color = Color.valueOf('5866AFFF')
				break

			case BlockComponent.BlockType.L:
				texture.region = lTex
				blockComponent.color = Color.valueOf('EF7922FF')
				break

			case BlockComponent.BlockType.O:
				texture.region = oTex
				blockComponent.color = Color.valueOf('F5D507FF')
				texture.initialOffsetX = 0
				texture.offsetX = texture.initialOffsetX
				break

			case BlockComponent.BlockType.S:
				texture.region = sTex
				blockComponent.color = Color.valueOf('40B73FFF')
				break

			case BlockComponent.BlockType.T:
				texture.region = tTex
				blockComponent.color = Color.valueOf('AE4D9EFF')
				break

			case BlockComponent.BlockType.Z:
				texture.region = zTex
				blockComponent.color = Color.valueOf('EF202BFF')
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
