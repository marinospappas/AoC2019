package mpdev.springboot.aoc2019.solutions.day18

import mpdev.springboot.aoc2019.utils.AocException
import java.awt.Point
import mpdev.springboot.aoc2019.utils.Graph
import mpdev.springboot.aoc2019.utils.GraphNode
import mpdev.springboot.aoc2019.utils.plus
import java.util.*
import kotlin.system.measureTimeMillis

class Vault(val input: List<String>) {

    companion object {
        val END_POINT = Point(Int.MAX_VALUE, Int.MAX_VALUE)
    }

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
    }

    fun getStart(): GraphNode<GraphKey> = GraphNode(GraphKey(start, 0)) { p -> getNeighbours(p)}
    fun getEnd(): GraphNode<GraphKey> =
        GraphNode(GraphKey(END_POINT, finalKeysList)){ p -> getNeighbours(p)}

    fun atEnd(id: GraphKey) = id.keys == finalKeysList

    var countGetNeighbours = 0
    var totalElapsed = 0L
    var cacheHits = 0

    val neighboursCache: MutableMap<GraphKey, List<GraphNode<GraphKey>>> = mutableMapOf()

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
        val neighbours = mutableListOf<GraphNode<GraphKey>>()
        ++countGetNeighbours
        totalElapsed += measureTimeMillis {
            val stack: Stack<Pair<Point, Int>> = Stack()     // position, distance
            val discovered: MutableSet<Point> = mutableSetOf()
            // find neighbouring keys using DFS algorithm
            val position = id.position
            val keys = id.keys
            var distance: Int
            stack.push(Pair(position, 0))
            while (stack.isNotEmpty()) {
                val topOfStack = stack.pop()
                val newPos = topOfStack.first
                distance = topOfStack.second
                if (!discovered.contains(newPos)) {
                    discovered.add(newPos)
                    ++distance
                    setOf(Point(0, 1), Point(0, -1), Point(1, 0), Point(-1, 0))
                        .forEach { pos ->
                            val thisData = data[newPos + pos]
                            if (thisData != null) {
                                if (thisData.vaultItem == VaultItem.EMPTY ||
                                    thisData.vaultItem == VaultItem.START ||
                                    (thisData.vaultItem == VaultItem.KEY && keys.containsKey(thisData.value)) ||
                                    (thisData.vaultItem == VaultItem.GATE && keys.containsKey(thisData.value.lowercaseChar()))
                                )
                                    stack.push(Pair(newPos + pos, distance))
                                else {
                                    if (thisData.vaultItem == VaultItem.KEY && !keys.containsKey(thisData.value)) {
                                        val neighbourId = GraphKey(newPos + pos, id.keys.addKey(thisData.value))
                                        neighbours.add(GraphNode(neighbourId) { p -> getNeighbours(p) })
                                        graph.updateCost(id, neighbourId, distance)
                                    }
                                }
                            }
                        }
                }
            }
        }
        //println("*** getNeighbours of $id : ${neighbours.size} ${neighbours.map { it.getId() }}")
        return neighbours
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

    data class GraphKey(var position: Point, var keys: Int) {
        override fun toString(): String {
            var keysList = ""
            ('a'..'z').forEach { if (keys.containsKey(it)) keysList += "$it " }
            return "[(x=${position.x},y=${position.y}) keys= $keysList]"
        }
    }
}

fun Int.addKey(k: Char) = this or(1 shl k - 'a')

fun Int.containsKey(k: Char) = this.shr(k - 'a') and 1 == 1

