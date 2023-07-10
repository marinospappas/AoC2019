package mpdev.springboot.aoc2019.solutions.day18

import mpdev.springboot.aoc2019.utils.AocException
import mpdev.springboot.aoc2019.utils.Graph
import mpdev.springboot.aoc2019.utils.GraphNode
import java.awt.Point
import kotlin.system.measureTimeMillis

class VaultPart2(input: List<String>) : Vault(input) {

    ///////// part 2

    lateinit var graph2: Graph<GraphKey2>
    lateinit var startList: MutableList<Point>

    fun getStart2() = GraphNode(GraphKey2(startList, 0)) { p -> getNeighbours(p)}
    fun atEnd(id: GraphKey2) = id.keys == finalKeysList

    fun createGraph2() {
        graph2 = Graph { p -> getNeighbours(p) }
        startList =  data.entries.filter { it.value.vaultItem == VaultItem.START }.map { it.key }.toMutableList()
        graph2.addNode(GraphKey2(startList, 0))
        countGetNeighbours = 0
        countFindNeighbours = 0
        countCalcNeighbours = 0
        totalElapsed = 0L
        cacheHits = 0
        keysGraphCache.clear()
        neighboursCache.clear()
    }

    // 2nd level cache - holds all the reachable keys from the position of a certain key
    var neighbours2Cache: MutableMap<GraphKey2, List<GraphNode<GraphKey2>>> = mutableMapOf()

    // 1st level cache - holds all the possible keys and their positions and distances
    // that can be reached if we ignore keys or gates
    // also holds the information about what keys and what gates are in between
    // this is the same keys graph cache variable as in Part 1

    /**
     * basic getNeighbours (reachable keys) function for input: GraphKey
     * uses level 2 cache (list of reachable keys for a graphKey)
     * if requested value is not cached then findNeighbours is called
     */
    fun getNeighbours(id: GraphKey2): List<GraphNode<GraphKey2>> {
        ++countGetNeighbours
        val neighbourList: List<GraphNode<GraphKey2>>
        totalElapsed += measureTimeMillis {
            // 2nd level cache is checked here
            val cached = neighbours2Cache[id]
            neighbourList = if (cached != null)
                cached
            else {
                val neighbours = findNeighbours(id)
                neighbours2Cache[id] = neighbours
                neighbours
            }
        }
        return neighbourList
    }

    /**
     * findNeighbours (reachable keys) for input: GraphKey
     * uses level 1 cache (list of all possible keys regardless of gates or other keys) from a position
     * if cache entry exists for this position then the reachable neighbours are calculated by
     * filtering this entry based on keys in possession
     * if not then calculateNeighbours is called
     */
    private fun findNeighbours(id: GraphKey2): List<GraphNode<GraphKey2>> {
        ++countFindNeighbours
        // 1st level cache (keys Graph) is checked here
        id.positions.forEach { position ->
            if (keysGraphCache[position] == null) {
                ++countCalcNeighbours
                calculateAndCacheNeighbours(position)
            }
        }
        return getNeighboursFromCache(id)
    }

    /**
     * getNeighboursFromCache uses level 1 cache and filters based on keys in possession
     * to find the reachable neighbour keys from input: GraphKey
     * also updates costs to each neighbour key
     */
    private fun getNeighboursFromCache(id: GraphKey2): List<GraphNode<GraphKey2>> {
        val reachableKeysTotal = mutableMapOf<Int,List<KeysGraphNode>>()
        val keysInPossession = id.keys
        id.positions.indices.forEach { posIndx ->
            val keyGraph = keysGraphCache[id.positions[posIndx]] ?: throw AocException("could not locate key cache entry for $id")
            val reachableKeys = keyGraph.filter { destKey ->
                (destKey.keyConstraint.isEmpty() || keysInPossession.containsAllKeys(destKey.keyConstraint))
                        && (destKey.gateConstraint.isEmpty() || keysInPossession.containsAllKeys(destKey.gateConstraint.map { it.lowercaseChar() }))
                        && (!keysInPossession.containsKey(destKey.neighbourKey))
            }
            reachableKeysTotal[posIndx] = reachableKeys
            // add new graph nodes and costs
            reachableKeys.forEach {
                val newPosList = id.positions.toMutableList()
                newPosList[posIndx] = it.neighbourPos
                val nodeId = GraphKey2(newPosList, keysInPossession.addKey(it.neighbourKey))
                graph2.addNode(nodeId)
                graph2.updateCost(id, nodeId, it.distance)
            }
        }
        return reachableKeysTotal.entries.flatMap { entry ->
            val index = entry.key
            val value = entry.value
            value.map {
                val newPosList = id.positions.toMutableList()
                newPosList[index] = it.neighbourPos
                GraphNode(GraphKey2(newPosList, keysInPossession.addKey(it.neighbourKey))) { p -> getNeighbours(p) }
            }
        }
    }

    data class GraphKey2(var positions: MutableList<Point> = mutableListOf(), override var keys: Int = 0): GraphBasicKey(keys) {
        override fun toString() = "[${positions.map { "(x=${it.x},y=${it.y}) " }} keys= ${keys.keysList()}]"
    }

}