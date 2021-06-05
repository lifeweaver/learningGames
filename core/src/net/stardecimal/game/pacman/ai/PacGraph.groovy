package net.stardecimal.game.pacman.ai

import com.badlogic.gdx.ai.pfa.Connection
import com.badlogic.gdx.ai.pfa.DefaultConnection
import com.badlogic.gdx.ai.pfa.DefaultGraphPath
import com.badlogic.gdx.ai.pfa.GraphPath
import com.badlogic.gdx.ai.pfa.Heuristic
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder
import com.badlogic.gdx.ai.pfa.indexed.IndexedGraph
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.ObjectMap
import net.stardecimal.game.ai.DefaultHeuristic
import net.stardecimal.game.ai.GenericNode

class PacGraph implements IndexedGraph<GenericNode> {
	Array<GenericNode> nodes = new Array<>()
	Array<DefaultConnection> paths = new Array<>()

	ObjectMap<GenericNode, Array<Connection<GenericNode>>> map = new ObjectMap<>()
	int lastNodeIndex = 0

	void addNode(GenericNode node) {
		node.index = lastNodeIndex
		lastNodeIndex++

		nodes.add(node)
	}

	void connectNodes(GenericNode fromNode, GenericNode toNode) {
		DefaultConnection path = new DefaultConnection(fromNode, toNode)
		if(!map.containsKey(fromNode)) {
			map.put(fromNode, new Array<Connection<GenericNode>>())
		}
		map.get(fromNode).add(path)
		paths.add(path)
	}

	GraphPath<GenericNode> findPath(GenericNode startNode, GenericNode goalNode, Heuristic heuristic=new DefaultHeuristic()) {
		GraphPath<GenericNode> nodePath = new DefaultGraphPath<>()
		new IndexedAStarPathFinder<>(this).searchNodePath(startNode, goalNode, heuristic, nodePath)
		return nodePath
	}

	@Override
	int getIndex(GenericNode node) {
		return node ? node.index : 0
	}

	@Override
	int getNodeCount() {
		return lastNodeIndex
	}

	@Override
	Array<Connection<GenericNode>> getConnections(GenericNode fromNode) {
		if(map.containsKey(fromNode)) {
			return map.get(fromNode)
		}
		return new Array<>(0)
	}
}
