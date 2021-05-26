package net.stardecimal.game.tetris.entity.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

class BlockComponent implements Component, Pool.Poolable {
	static enum BlockType { I, J, L, O, S, T, Z }
	BlockType blockType = null

	//3 wide block types
	static List<BlockType> threeWideBlocks = [
			BlockType.J,
			BlockType.L,
			BlockType.S,
			BlockType.T,
			BlockType.Z
	]

	@Override
	void reset() {
		blockType = null
	}
}
