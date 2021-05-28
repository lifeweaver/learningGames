package net.stardecimal.game.tetris.entity.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.utils.Pool

class BlockComponent implements Component, Pool.Poolable {
	static enum BlockType { I, J, L, O, S, T, Z }
	BlockType type = null
	Color color = null

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
		type = null
		color = null
	}
}
