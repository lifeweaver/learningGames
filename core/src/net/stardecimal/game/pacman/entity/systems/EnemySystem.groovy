package net.stardecimal.game.pacman.entity.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.ai.pfa.DefaultGraphPath
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
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class EnemySystem extends IteratingSystem {
	LevelFactory levelFactory
	private Array<Entity> enemyQueue
	PacGraph graph
	long lastRepath = System.currentTimeMillis()
	GraphPath<GenericNode> blinkyPatrolPath, pinkyPatrolPath, inkyPatrolPath, clydePatrolPath
	private static final Logger log = LoggerFactory.getLogger(EnemySystem)

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
			StateComponent stateComponent = Mapper.stateCom.get(it)
			stateComponent.time -= deltaTime
			if(!aiComponent.graphPath) {
				aiComponent.graphPath = determinePath(it, stateComponent)
			} else {
				if(System.currentTimeMillis() - lastRepath > 100) {
					aiComponent.graphPath = determinePath(it, stateComponent)
				}
			}

			//Ghosts movement
			GenericNode start = getNode(it)
			int nextIndex = aiComponent.graphPath.findIndexOf {
				it == start
			} + 1

			nextIndex = aiComponent.graphPath?.size() == nextIndex ? 0 : nextIndex
			GenericNode next = ((GraphPath<GenericNode>) aiComponent.graphPath).get(nextIndex)

			if(next) {
				Vector2 nextGamePos = levelFactory.gamePosition(next.x as int, next.y as int)

				//Update the steering to move toward the next location
				SteeringComponent steeringComponent = Mapper.sCom.get(it)
				SteeringBehavior<Vector2> steeringBehavior = new Seek<Vector2>(steeringComponent, new SdLocation(position: nextGamePos, orientation: 0))
				steeringComponent.steeringBehavior = steeringBehavior
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

	GenericNode getNode(Vector2 tilePos) {
		return (GenericNode) graph.nodes.find { it.x == tilePos.x && it.y == tilePos.y}
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

	GraphPath<GenericNode> buildPath(GenericNode start, GenericNode dest, Entity entity) {
		//Set to random if not set
		dest = dest ?: graph.nodes.get(levelFactory.rand.nextInt(graph.nodes.size))
		if(start && dest) {
			return graph.findPath(start, dest, Mapper.aiCom.get(entity)?.heuristic)
		} else {
			log.debug("Either start and dest were null: start: ${start}, dest: ${dest}")
		}
		return null
	}

	GraphPath<GenericNode> determinePatrolPath(Entity entity, GenericNode start) {
		GenericNode dest = null

		//Update to patrol path, each ghost has a different one
		switch(Mapper.typeCom.get(entity).type) {
			case TypeComponent.TYPES.BLINKY:
				//Top right
				buildBlinkyPatrolPath()

				//send to patrol node
				//if already on patrol path, return it
				if(blinkyPatrolPath.contains(start)) {
					return blinkyPatrolPath
				}

				//else get a path to the patrol path
				dest = blinkyPatrolPath.first()
				break

			case TypeComponent.TYPES.PINKY:
				//Top left
				buildPinkyPatrolPath()

				//send to patrol node
				//if already on patrol path, return it
				if(pinkyPatrolPath.contains(start)) {
					return pinkyPatrolPath
				}

				//else get a path to the patrol path
				dest = pinkyPatrolPath.first()
				break

			case TypeComponent.TYPES.INKY:
				//Bottom right
				buildInkyPatrolPath()

				//send to patrol node
				//if already on patrol path, return it
				if(inkyPatrolPath.contains(start)) {
					return inkyPatrolPath
				}

				//else get a path to the patrol path
				dest = inkyPatrolPath.first()
				break

			case TypeComponent.TYPES.CLYDE:
				//Bottom left
				buildClydePatrolPath()

				//send to patrol node
				//if already on patrol path, return it
				if(clydePatrolPath.contains(start)) {
					return clydePatrolPath
				}

				//else get a path to the patrol path
				dest = clydePatrolPath.first()
				break
		}

		return buildPath(start, dest, entity)
	}

	GraphPath<GenericNode> determinePath(Entity entity, StateComponent stateComponent) {
		GenericNode start = getNode(entity)
		GenericNode playerNode = getNode(levelFactory.player)
		float powerUpActiveTime = levelFactory.powerCom.get(levelFactory.player)?.activeTime ?: 0
		GenericNode dest = null

		/*
		 * Should ghosts only flee if they see the player powered up?
		 * Should ghosts only attack if they can actually see the player?
		 */


		//Check if we should switch on the fleeing
		if(powerUpActiveTime > 0 && Vector2.dst(start.x, start.y, playerNode.x, playerNode.y) < 5) {
			stateComponent.state = StateComponent.STATE_FLEEING
			log.debug("threat detected, new active state: STATE_FLEEING")

			if(stateComponent.state == StateComponent.STATE_FLEEING) {
				//Keep fleeing
				//Build path using node farthest away from player
				dest = graph.nodes.sort {
					Vector2.dst(it.x, it.y, playerNode.x, playerNode.y)
				}.last()
			}
		} else if(powerUpActiveTime <= 0 && stateComponent.state == StateComponent.STATE_FLEEING) {
			stateComponent.state = StateComponent.STATE_PATROL
		}

		//Start the patrol
		if(stateComponent.state == StateComponent.STATE_NORMAL) {
			stateComponent.state = StateComponent.STATE_PATROL

			//Only patrol for a certain amount of seconds
			stateComponent.time = 20
		}

		if(stateComponent.state == StateComponent.STATE_PATROL) {
			if(stateComponent.time < 0) {
				//Switch to seeking if the patrol time is up
				stateComponent.state = StateComponent.STATE_SEEKING
			} else {
				return determinePatrolPath(entity, start)
			}
		}

		if(stateComponent.state == StateComponent.STATE_SEEKING) {
			dest = getNode(levelFactory.player)
			if(dest) {
				Vector2 newPos = null
				//TODO: edit dest based on entity type, i.e pinky will try to ambush
				switch(Mapper.typeCom.get(entity).type) {
					case TypeComponent.TYPES.BLINKY:
						//Keep default dest
						break

					case TypeComponent.TYPES.PINKY:
						newPos = customPinkySeeking(dest)
						break

					case TypeComponent.TYPES.INKY:
//						newPos = customInkySeeking(dest)
						break

					case TypeComponent.TYPES.CLYDE:
						break
				}
				dest = newPos ? getNode(newPos) : dest
			}
		}

		return buildPath(start, dest, entity)
	}

	void buildBlinkyPatrolPath() {
		if(blinkyPatrolPath) {
			return
		}

		blinkyPatrolPath = new DefaultGraphPath<>()
		[
				new Vector2(26.0,31.0),
				new Vector2(26.0,30.0),
				new Vector2(26.0,29.0),
				new Vector2(26.0,28.0),
				new Vector2(26.0,27.0),
				new Vector2(25.0,27.0),
				new Vector2(24.0,27.0),
				new Vector2(23.0,27.0),
				new Vector2(22.0,27.0),
				new Vector2(21.0,27.0),
				new Vector2(21.0,28.0),
				new Vector2(21.0,29.0),
				new Vector2(21.0,30.0),
				new Vector2(21.0,31.0),
				new Vector2(22.0,31.0),
				new Vector2(23.0,31.0),
				new Vector2(24.0,31.0),
				new Vector2(25.0,31.0)
		].each {tilePos ->
			GenericNode foundNode = getNode(tilePos)
			blinkyPatrolPath.add(foundNode)
		}
	}

	void buildPinkyPatrolPath() {
		if(pinkyPatrolPath) {
			return
		}

		pinkyPatrolPath = new DefaultGraphPath<>()
		[
				new Vector2(1.0,31.0),
				new Vector2(2.0,31.0),
				new Vector2(3.0,31.0),
				new Vector2(4.0,31.0),
				new Vector2(5.0,31.0),
				new Vector2(6.0,31.0),
				new Vector2(6.0,30.0),
				new Vector2(6.0,29.0),
				new Vector2(6.0,28.0),
				new Vector2(6.0,27.0),
				new Vector2(5.0,27.0),
				new Vector2(4.0,27.0),
				new Vector2(3.0,27.0),
				new Vector2(2.0,27.0),
				new Vector2(1.0,27.0),
				new Vector2(1.0,28.0),
				new Vector2(1.0,29.0),
				new Vector2(1.0,30.0)
		].each {tilePos ->
			GenericNode foundNode = getNode(tilePos)
			pinkyPatrolPath.add(foundNode)
		}
	}

	void buildInkyPatrolPath() {
		if(inkyPatrolPath) {
			return
		}

		inkyPatrolPath = new DefaultGraphPath<>()
		[
				new Vector2(26.0,3.0),
				new Vector2(25.0,3.0),
				new Vector2(24.0,3.0),
				new Vector2(23.0,3.0),
				new Vector2(22.0,3.0),
				new Vector2(21.0,3.0),
				new Vector2(20.0,3.0),
				new Vector2(19.0,3.0),
				new Vector2(18.0,3.0),
				new Vector2(17.0,3.0),
				new Vector2(16.0,3.0),
				new Vector2(15.0,3.0),
				new Vector2(15.0,4.0),
				new Vector2(15.0,5.0),
				new Vector2(15.0,6.0),
				new Vector2(16.0,6.0),
				new Vector2(17.0,6.0),
				new Vector2(18.0,6.0),
				new Vector2(18.0,7.0),
				new Vector2(18.0,8.0),
				new Vector2(18.0,9.0),
				new Vector2(19.0,9.0),
				new Vector2(20.0,9.0),
				new Vector2(21.0,9.0),
				new Vector2(21.0,8.0),
				new Vector2(21.0,7.0),
				new Vector2(21.0,6.0),
				new Vector2(22.0,6.0),
				new Vector2(23.0,6.0),
				new Vector2(24.0,6.0),
				new Vector2(25.0,6.0),
				new Vector2(26.0,6.0),
				new Vector2(26.0,5.0),
				new Vector2(26.0,4.0)
		].each {tilePos ->
			GenericNode foundNode = getNode(tilePos)
			inkyPatrolPath.add(foundNode)
		}
	}

	void buildClydePatrolPath() {
		if(clydePatrolPath) {
			return
		}

		clydePatrolPath = new DefaultGraphPath<>()
		[
				new Vector2(1.0,3.0),
				new Vector2(2.0,3.0),
				new Vector2(3.0,3.0),
				new Vector2(4.0,3.0),
				new Vector2(5.0,3.0),
				new Vector2(6.0,3.0),
				new Vector2(7.0,3.0),
				new Vector2(8.0,3.0),
				new Vector2(9.0,3.0),
				new Vector2(10.0,3.0),
				new Vector2(11.0,3.0),
				new Vector2(12.0,3.0),
				new Vector2(12.0,4.0),
				new Vector2(12.0,5.0),
				new Vector2(12.0,6.0),
				new Vector2(11.0,6.0),
				new Vector2(10.0,6.0),
				new Vector2(9.0,6.0),
				new Vector2(9.0,7.0),
				new Vector2(9.0,8.0),
				new Vector2(9.0,9.0),
				new Vector2(8.0,9.0),
				new Vector2(7.0,9.0),
				new Vector2(6.0,9.0),
				new Vector2(6.0,8.0),
				new Vector2(6.0,7.0),
				new Vector2(6.0,6.0),
				new Vector2(5.0,6.0),
				new Vector2(4.0,6.0),
				new Vector2(3.0,6.0),
				new Vector2(2.0,6.0),
				new Vector2(1.0,6.0),
				new Vector2(1.0,5.0),
				new Vector2(1.0,4.0)
		].each {tilePos ->
			GenericNode foundNode = getNode(tilePos)
			clydePatrolPath.add(foundNode)
		}
	}

	Vector2 customPinkySeeking(GenericNode dest) {
		//Four tiles ahead of the direction the player is facing
		//If it's not a navigable tile, just minus one until it's valid
		float x = 0
		float y = 0
		Vector2 newPos = new Vector2()
		switch(Mapper.transCom.get(levelFactory.player)?.rotation) {
			case 0: //left
				x = -4
				newPos = new Vector2(dest.x + x as float, dest.y + y as float)
				while(!levelFactory.isCell('node', true, newPos)) {
					newPos.x++
				}
				break

			case 90: //down
				y = -4
				newPos = new Vector2(dest.x + x as float, dest.y + y as float)
				while(!levelFactory.isCell('node', true, newPos)) {
					newPos.y++
				}
				break

			case 180: //right
				x = 4
				newPos = new Vector2(dest.x + x as float, dest.y + y as float)
				while(!levelFactory.isCell('node', true, newPos)) {
					newPos.x--
				}
				break

			case 270: //up
				y = 4
				newPos = new Vector2(dest.x + x as float, dest.y + y as float)
				while(!levelFactory.isCell('node', true, newPos)) {
					newPos.y--
				}
				break
		}
		return newPos
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		enemyQueue.add(entity)
	}
}
