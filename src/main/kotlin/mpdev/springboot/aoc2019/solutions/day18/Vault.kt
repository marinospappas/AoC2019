package mpdev.springboot.aoc2019.solutions.day18

import mpdev.springboot.aoc2019.utils.AocException
import java.awt.Point
import mpdev.springboot.aoc2019.utils.Graph
import mpdev.springboot.aoc2019.utils.GraphNode
import mpdev.springboot.aoc2019.utils.plus
import kotlin.collections.ArrayDeque
import kotlin.system.measureTimeMillis

open class Vault(val input: List<String>) {

    var data: MutableMap<Point, VaultPoint> = mutableMapOf()
    var finalKeysList: Int

    var maxX = 0
    var maxY = 0
    private var start = Point(0,0)

    init {
        input.indices.forEach { y ->
            input[y].indices.forEach { x ->
                if (input[y][x] != ' ') {
                    data[Point(x, y)] = when (input[y][x]) {
                        '.' -> VaultPoint(VaultItem.EMPTY, input[y][x])
                        '#' -> VaultPoint(VaultItem.WALL, input[y][x])
                        '@' -> {
                            start = Point(x, y)
                            VaultPoint(VaultItem.START, input[y][x])
                        }
                        in 'a'..'z' -> VaultPoint(VaultItem.KEY, input[y][x])
                        in 'A'..'Z' -> VaultPoint(VaultItem.GATE, input[y][x])
                        else -> throw AocException("cannot process input line ${input[y]}")
                    }
                }
            }
        }
        maxX = input.first().length
        maxY = input.size
        finalKeysList = 0
        data.entries.filter { it.value.vaultItem == VaultItem.KEY }.map { it.value.value }
            .forEach { key -> finalKeysList = finalKeysList.addKey(key) }
    }

    ///////// part 1
    lateinit var graph: Graph<GraphKey>

    fun createGraph() {
        graph = Graph { p -> getNeighbours(p) }
        graph.addNode(GraphKey(start, 0))
        countGetNeighbours = 0
        countFindNeighbours = 0
        countCalcNeighbours = 0
        totalElapsed = 0L
        cacheHits = 0
        keysGraphCache.clear()
        neighboursCache.clear()
    }

    open fun getStart(): GraphNode<GraphKey> = GraphNode(GraphKey(start, 0)) { p -> getNeighbours(p)}
    fun atEnd(id: GraphKey) = id.keys == finalKeysList

    var countGetNeighbours = 0
    var countFindNeighbours = 0
    var countCalcNeighbours = 0
    var totalElapsed = 0L
    var cacheHits = 0

    // 2nd level cache - holds all the reachable keys from the position of a certain key
    var neighboursCache: MutableMap<GraphKey, List<GraphNode<GraphKey>>> = mutableMapOf()

    // 1st level cache - holds all the possible keys and their positions and distances
    // that can be reached if we ignore keys or gates
    // also holds the information about what keys and what gates are in between
    var keysGraphCache: MutableMap<Point,List<KeysGraphNode>> = mutableMapOf()

    /**
     * basic getNeighbours (reachable keys) function for input: GraphKey
     * uses level 2 cache (list of reachable keys for a graphKey)
     * if requested value is not cached then findNeighbours is called
     */
    fun getNeighbours(id: GraphKey): List<GraphNode<GraphKey>> {
        ++countGetNeighbours
        val neighbourList: List<GraphNode<GraphKey>>
        totalElapsed += measureTimeMillis {
            // 2nd level cache is checked here
            val cached = neighboursCache[id]
            neighbourList = if (cached != null)
                cached
            else {
                val neighbours = findNeighbours(id)
                neighboursCache[id] = neighbours
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
    private fun findNeighbours(id: GraphKey): List<GraphNode<GraphKey>> {
        ++countFindNeighbours
        // 1st level cache (keys Graph) is checked here
        if (keysGraphCache[id.position] == null) {
            ++countCalcNeighbours
            calculateAndCacheNeighbours(id.position)
        }
        return getNeighboursFromCache(id)
    }

    /**
     * calculateNeighbours (all possible keys) for input: position
     * calculates all possible keys that can be reached from a position regardless of other keys or gates in the way
     * any keys or gates found in the way are also saved as constraints
     * updates the level 1 cache
     */
    protected fun calculateAndCacheNeighbours(position: Point) {
        val neighboursToCache = mutableListOf<KeysGraphNode>()
        val queue = ArrayDeque<Pair<Point, KeysGraphNode>>()     // position, destination key details (id, distance, constraints)
        val discovered: MutableSet<Point> = mutableSetOf()
        // find neighbouring keys using BFS algorithm
        var distance: Int
        queue.add(Pair(position, KeysGraphNode()))
        discovered.add(position)
        while (queue.isNotEmpty()) {
            val node = queue.removeFirst()
            distance = node.second.distance + 1
            setOf(Point(1, 0), Point(0, 1), Point(-1, 0), Point(0, -1)).forEach { pos ->
                val newPos = node.first + pos
                val thisData = data[newPos]
                val gateConstraint = node.second.gateConstraint.toMutableList()
                val keyConstraint = node.second.keyConstraint.toMutableList()
                if (thisData == null || thisData.vaultItem == VaultItem.WALL)
                    return@forEach
                if (!discovered.contains(newPos)) {
                    discovered.add(newPos)
                    if (thisData.vaultItem == VaultItem.GATE)   // if gate then add constraint
                        gateConstraint.add(thisData.value)
                    if (thisData.vaultItem == VaultItem.KEY) {  // if key then add to cache
                        neighboursToCache.add(
                            KeysGraphNode(newPos, thisData.value, distance, keyConstraint.toList(), gateConstraint.toList())
                        )
                        // but also add constraint to continue further down the maze
                        keyConstraint.add(thisData.value)
                    }
                    queue.add(
                        Pair(newPos, KeysGraphNode(newPos, '0', distance, keyConstraint, gateConstraint))
                    )
                }
            }
        }
        keysGraphCache[position] = neighboursToCache
    }

    /**
     * getNeighboursFromCache uses level 1 cache and filters based on keys in possession
     * to find the reachable neighbour keys from input: GraphKey
     * also updates costs to each neighbour key
     */
    protected fun getNeighboursFromCache(id: GraphKey): List<GraphNode<GraphKey>> {
        val keyGraph = keysGraphCache[id.position] ?: throw AocException("could not locate key cache entry for $id")
        val keysInPossession = id.keys
        val reachableKeys = keyGraph.filter { destKey ->
            (destKey.keyConstraint.isEmpty() || keysInPossession.containsAllKeys(destKey.keyConstraint))
                    && (destKey.gateConstraint.isEmpty() || keysInPossession.containsAllKeys(destKey.gateConstraint.map { it.lowercaseChar() }))
                    && (!keysInPossession.containsKey(destKey.neighbourKey))
        }
        // add new graph nodes and costs
        reachableKeys.forEach {
            val nodeId = GraphKey(it.neighbourPos, keysInPossession.addKey(it.neighbourKey))
            graph.addNode(nodeId)
            graph.updateCost(id, nodeId, it.distance)
        }
        // and return reachable keys
        return reachableKeys.map { GraphNode(GraphKey(it.neighbourPos, keysInPossession.addKey(it.neighbourKey))) { p -> getNeighbours(p) } }
    }

    private fun vault2Grid(maze: Map<Point, VaultPoint>): Array<CharArray> {
        val grid: Array<CharArray> = Array(maxY) { CharArray(maxX) { ' ' } }
        maze.forEach { (pos, item) -> grid[pos.y][pos.x] = when (item.vaultItem) {
                VaultItem.EMPTY -> '.'
                VaultItem.WALL -> '#'
                VaultItem.GATE -> item.value
                VaultItem.KEY -> item.value
                VaultItem.START -> item.value
            }
        }
        return grid
    }

    fun printVault() {
        val grid = vault2Grid(data)
        for (i in grid.indices) {
            print("${String.format("%2d",i)} ")
            for (j in grid.first().indices)
                print(grid[i][j])
            println()
        }
        print("   ")
        for (i in grid.first().indices)
            print(if (i%10 == 0) i/10 else " ")
        println()
        print("   ")
        for (i in grid.first().indices)
            print(i%10)
        println()
    }

    data class GraphKey(var position: Point = Point(0,0), override var keys: Int = 0): GraphBasicKey(keys) {
        override fun toString() = "[(x=${position.x},y=${position.y}) keys= ${keys.keysList()}]"
    }

    open class GraphBasicKey(open var keys: Int = 0)

    data class KeysGraphNode(val neighbourPos: Point = Point(0,0),
                             val neighbourKey: Char = '0',
                             val distance: Int = 0,
                             val keyConstraint: List<Char> = listOf(),
                             val gateConstraint: List<Char> = listOf())
}

fun Int.addKey(k: Char) = this or(1 shl k - 'a')
fun Int.containsKey(k: Char) = this.shr(k - 'a') and 1 == 1
fun Int.containsAllKeys(keys: List<Char>): Boolean {
    keys.forEach { if (this.shr(it - 'a') and 1 != 1) return false }
    return true
}
fun Int.keysList(): String {
    var keysList = ""
    ('a'..'z').forEach { if (this.containsKey(it)) keysList += "$it " }
    return keysList
}

