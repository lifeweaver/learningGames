package net.stardecimal.game.pacman.entity.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.ai.pfa.GraphPath
import com.badlogic.gdx.ai.steer.SteeringBehavior
import com.badlogic.gdx.ai.steer.behaviors.Seek
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Array
import net.stardecimal.game.ai.GenericNode
import net.stardecimal.game.entity.components.AiComponent
import net.stardecimal.game.entity.components.EnemyComponent
import net.stardecimal.game.entity.components.Mapper
import net.stardecimal.game.entity.components.SdBodyComponent
import net.stardecimal.game.entity.components.SdLocation
import net.stardecimal.game.entity.components.StateComponent
import net.stardecimal.game.entity.components.SteeringComponent
import net.stardecimal.game.entity.components.TypeComponent
import net.stardecimal.game.pacman.LevelFactory
import net.stardecimal.game.pacman.ai.PacGraph

class EnemySystem extends IteratingSystem {
	LevelFactory levelFactory
	private Array<Entity> enemyQueue
	PacGraph graph
	long lastRepath = System.currentTimeMillis()

	@SuppressWarnings("unchecked")
	EnemySystem(LevelFactory lvlFactory) {
		super(Family.all(EnemyComponent.class).get())
		this.levelFactory = lvlFactory
		enemyQueue = new Array<Entity>()
		graph = new PacGraph()
		buildNodeMap()
	}

	@Override
	void update(float deltaTime) {
		super.update(deltaTime)
		createGhostsIfAnyDead()

		enemyQueue.each {
			//Update ghost goal
			AiComponent aiComponent = Mapper.aiCom.get(it)
			if(!aiComponent.graphPath) {
				aiComponent.graphPath = buildPath(it)
			} else {
				if(System.currentTimeMillis() - lastRepath > 100) {
					aiComponent.graphPath = buildPath(it)
				}
			}

			//Ghosts movement
			GenericNode start = getNode(it)
			int nextIndex = aiComponent.graphPath.findIndexOf {
				it == start
			} + 1

			nextIndex = aiComponent.graphPath?.size() == nextIndex ? nextIndex - 1 : nextIndex
			GenericNode next = ((GraphPath<GenericNode>) aiComponent.graphPath).get(nextIndex)

			if(next) {
				Vector2 nextGamePos = levelFactory.gamePosition(next.x as int, next.y as int)

				//Update the steering to move toward the next location
				SteeringComponent steeringComponent = Mapper.sCom.get(it)
				//TODO: look at pursue
				SteeringBehavior<Vector2> steeringBehavior = new Seek<Vector2>(steeringComponent,new SdLocation(position: nextGamePos, orientation: 0))
				steeringComponent.steeringBehavior = steeringBehavior

				StateComponent stateComponent = Mapper.stateCom.get(it)
				stateComponent.state = StateComponent.STATE_MOVING
			}
		}

		//Update the last repath timer
		if(System.currentTimeMillis() - lastRepath > 100) {
			lastRepath = System.currentTimeMillis()
		}

		enemyQueue.clear()
	}

	GenericNode getNode(Entity entity) {
		if(entity) {
			SdBodyComponent sdBody = Mapper.bCom.get(entity)
			if(sdBody) {
				Vector2 tilePos = levelFactory.tilePosition(sdBody.body.position)
				return (GenericNode) graph.nodes.find { it.x == tilePos.x && it.y == tilePos.y}
			}
		}
		return null
	}

	void createGhostsIfAnyDead() {
		if(enemyQueue.size < 4) {
			[TypeComponent.TYPES.BLINKY, TypeComponent.TYPES.PINKY, TypeComponent.TYPES.INKY, TypeComponent.TYPES.CLYDE].eachWithIndex {type, i ->
				if(!enemyQueue.find { Mapper.typeCom.get(it).type == type }) {
					levelFactory.createGhost(levelFactory.gamePosition(12 + i, 18), type)
				}
			}
		}
	}

	void buildNodeMap() {
		levelFactory.collisionLayer.width.times {x ->
			levelFactory.collisionLayer.height.times {y ->
				TiledMapTileLayer.Cell cell = levelFactory.collisionLayer.getCell(x, y)
				if(cell.tile.properties.get('node')) {
					GenericNode node = graph.nodes.find {it.x == x && it.y == y} as GenericNode
					if(!node) {
						node = new GenericNode(x: x, y: y)
						graph.addNode(node)
					}

					//Connection to any surrounding nodes
					if(levelFactory.isCell('node', true, new Vector2(x + 1, y))) {
						//Connect to original node
						GenericNode rightSideNode = graph.nodes.find {it.x == x + 1 && it.y == y} as GenericNode
						if(!rightSideNode) {
							rightSideNode = new GenericNode(x: x + 1, y: y)
							graph.addNode(rightSideNode)
						}

						graph.connectNodes(node, rightSideNode)
					}

					if(levelFactory.isCell('node', true, new Vector2(x - 1, y))) {
						//Connect to original node
						GenericNode leftSideNode = graph.nodes.find {it.x == x - 1 && it.y == y} as GenericNode
						if(!leftSideNode) {
							leftSideNode = new GenericNode(x: x - 1, y: y)
							graph.addNode(leftSideNode)
						}

						graph.connectNodes(node, leftSideNode)
					}

					if(levelFactory.isCell('node', true, new Vector2(x, y + 1))) {
						//Connect to original node
						GenericNode aboveNode = graph.nodes.find {it.x == x && it.y == y + 1} as GenericNode
						if(!aboveNode) {
							aboveNode = new GenericNode(x: x, y: y + 1)
							graph.addNode(aboveNode)
						}

						graph.connectNodes(node, aboveNode)
					}

					if(levelFactory.isCell('node', true, new Vector2(x, y - 1))) {
						//Connect to original node
						GenericNode belowNode = graph.nodes.find {it.x == x && it.y == y - 1} as GenericNode
						if(!belowNode) {
							belowNode = new GenericNode(x: x, y: y - 1)
							graph.addNode(belowNode)
						}

						graph.connectNodes(node, belowNode)
					}
				}
			}
		}
	}

	GraphPath<GenericNode> buildPath(Entity entity) {
		GenericNode start = getNode(entity)
		//TODO: If it can't find the player node, start patrol?
		GenericNode dest = getNode(levelFactory.player) ?: graph.nodes.get(levelFactory.rand.nextInt(graph.nodes.size))
		if(start && dest) {
			return graph.findPath(start, dest, Mapper.aiCom.get(entity)?.heuristic)
		} else {
			println("Either start and dest were null: start: ${start}, dest: ${dest}")
		}
		return null
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		enemyQueue.add(entity)
	}
}
