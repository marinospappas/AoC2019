package mpdev.springboot.aoc2019.solutions.day18

import mpdev.springboot.aoc2019.utils.AocException
import java.awt.Point
import mpdev.springboot.aoc2019.utils.Graph
import mpdev.springboot.aoc2019.utils.GraphNode
import mpdev.springboot.aoc2019.utils.plus
import kotlin.collections.ArrayDeque
import kotlin.system.measureTimeMillis

class Vault(val input: List<String>) {

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
        data
            .filter { it.value.vaultItem == VaultItem.KEY }
            .forEach { (k,_) -> graph.addNode(GraphKey(k, 0)) }
        countGetNeighbours = 0
        totalElapsed = 0L
        cacheHits = 0
        keysGraphCache = mutableMapOf()
    }

    fun getStart(): GraphNode<GraphKey> = GraphNode(GraphKey(start, 0)) { p -> getNeighbours(p)}
    fun atEnd(id: GraphKey) = id.keys == finalKeysList

    var countGetNeighbours = 0
    var totalElapsed = 0L
    var cacheHits = 0

    val neighboursCache: MutableMap<GraphKey, List<GraphNode<GraphKey>>> = mutableMapOf()

    lateinit var keysGraphCache: MutableMap<Char,List<KeysGraphNode>>

    fun getNeighbours(id: GraphKey): List<GraphNode<GraphKey>> {
        val cached = neighboursCache[id]
        return if (cached != null) {
            ++cacheHits
            cached
        }
        else {
            val neighbours = findNeighbours(id)
            neighboursCache[id] = neighbours
            neighbours
        }
    }

    private fun findNeighbours(id: GraphKey): List<GraphNode<GraphKey>> {
        ++countGetNeighbours
        var neighbours: List<GraphNode<GraphKey>>
        totalElapsed += measureTimeMillis {
            // check for cached entry in the keys Graph
            val key = data[id.position]?.value
            if ( keysGraphCache[key] == null)
                calculateAndCacheNeighbours(id)
            neighbours = getNeighboursFromCache(keysGraphCache[key]!!, id.keys)
        }
        return neighbours
    }

    private fun calculateAndCacheNeighbours(id: GraphKey) {
        val neighboursToCache = mutableListOf<KeysGraphNode>()
        val queue = ArrayDeque<Pair<Point, KeysGraphNode>>()     // position, destination key details (id, distance, constraints)
        val discovered: MutableSet<Point> = mutableSetOf()
        // find neighbouring keys using BFS algorithm
        val position = id.position
        val keys = id.keys
        var distance: Int
        queue.add(Pair(position, KeysGraphNode()))
        while (queue.isNotEmpty()) {
            val node = queue.removeFirst()
            val newPos = node.first
            distance = node.second.distance + 1
            val gateConstraint = node.second.gateConstraint
            val keyConstraint = node.second.keyConstraint
            setOf(Point(1, 0), Point(0, 1), Point(-1, 0), Point(0, -1)).forEach { pos ->
                val thisData = data[newPos + pos]
                if (thisData == null || thisData.vaultItem == VaultItem.WALL)
                    return@forEach
                if (!discovered.contains(newPos + pos)) {
                    discovered.add(newPos + pos)
                    if (thisData.vaultItem == VaultItem.GATE)   // if gate then add constraint
                        gateConstraint.add(thisData.value)
                    if (thisData.vaultItem == VaultItem.KEY) {  // if key then add to cache
                        neighboursToCache.add(KeysGraphNode(
                            GraphKey(newPos+pos,0.addKey(thisData.value)), distance, keyConstraint, gateConstraint))
                    }
                    // but also add constraint to continue further down the maze
                    keyConstraint.add(thisData.value)
                    queue.add(Pair(newPos + pos, KeysGraphNode(
                        GraphKey(newPos+pos,0.addKey(thisData.value)), distance, keyConstraint, gateConstraint
                    )))
                }
            }
        }
    }

    private fun getNeighboursFromCache(keyGraph: List<KeysGraphNode>, keys: Int): List<GraphNode<GraphKey>> {
        return keyGraph.filter { destKey ->
            (destKey.keyConstraint.isEmpty() || keys.containsAllKeys(destKey.keyConstraint))
            && (destKey.gateConstraint.isEmpty() || keys.containsAllKeys(destKey.gateConstraint.map { it.lowercaseChar() }))
        }.map { GraphNode(it.neighbourId) { p -> getNeighbours(p) } }
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

    data class GraphKey(var position: Point = Point(0,0), var keys: Int = 0) {
        override fun toString(): String {
            var keysList = ""
            ('a'..'z').forEach { if (keys.containsKey(it)) keysList += "$it " }
            return "[(x=${position.x},y=${position.y}) keys= $keysList]"
        }
    }

    data class KeysGraphNode(val neighbourId: GraphKey = GraphKey(),
                             val distance: Int = 0,
                             val keyConstraint: MutableList<Char> = mutableListOf(),
                             val gateConstraint: MutableList<Char> = mutableListOf())
}

fun Int.addKey(k: Char) = this or(1 shl k - 'a')
fun Int.containsKey(k: Char) = this.shr(k - 'a') and 1 == 1
fun Int.containsAllKeys(keys: List<Char>): Boolean {
    keys.forEach { if (this.shr(it - 'a') and 1 != 1) return false }
    return true
}

