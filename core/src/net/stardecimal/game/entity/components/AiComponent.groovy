package net.stardecimal.game.entity.components

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.ai.pfa.GraphPath
import com.badlogic.gdx.ai.pfa.Heuristic
import com.badlogic.gdx.ai.pfa.indexed.IndexedGraph
import com.badlogic.gdx.utils.Pool

class AiComponent implements Component, Pool.Poolable {
	IndexedGraph graph = null
	GraphPath graphPath = null
	Heuristic heuristic = null

	@Override
	void reset() {
		graph = null
		graphPath = null
		heuristic = null
	}
}
