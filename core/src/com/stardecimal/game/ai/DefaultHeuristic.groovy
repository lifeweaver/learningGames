package com.stardecimal.game.ai

import com.badlogic.gdx.ai.pfa.Heuristic
import com.badlogic.gdx.math.Vector2

class DefaultHeuristic implements Heuristic<GenericNode> {

	@Override
	float estimate(GenericNode currentNode, GenericNode goalNode) {
		return Vector2.dst(currentNode.x, currentNode.y, goalNode.x, goalNode.y)
	}
}
