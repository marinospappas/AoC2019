package mpdev.springboot.aoc2019.solutions.day18

import mpdev.springboot.aoc2019.utils.AocException
import java.awt.Point
import mpdev.springboot.aoc2019.utils.Graph
import mpdev.springboot.aoc2019.utils.GraphNode
import mpdev.springboot.aoc2019.utils.plus

class Vault(val input: List<String>) {

    companion object {
        val END_POINT = Point(Int.MAX_VALUE, Int.MAX_VALUE)
    }

    var data: MutableMap<Point, VaultPoint> = mutableMapOf()
    var finalKeysList: Set<Char>

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
        finalKeysList = data.entries.filter { it.value.vaultItem == VaultItem.KEY }.map { it.value.value }.sorted().toSet()
    }

    ///////// part 1
    private lateinit var graph: Graph<GraphKey>

    fun createGraph() {
        graph = Graph { p -> getNeighbours(p)}
        data.forEach { (k,_) -> graph.addNode(GraphKey(k, mutableSetOf())) }
    }

    fun getStart(): GraphNode<GraphKey> = GraphNode(GraphKey(start, mutableSetOf())) { p -> getNeighbours(p)}
    fun getEnd(): GraphNode<GraphKey> =
        GraphNode(GraphKey(END_POINT, mutableSetOf<Char>().also { it.addAll(finalKeysList) })){ p -> getNeighbours(p)}

    private fun getNeighbours(id: GraphKey): List<GraphNode<GraphKey>> {
        val neighbours = mutableListOf<GraphNode<GraphKey>>()
        val position = id.position
        // get neighbours in the surrounding area
        setOf(Point(0,1), Point(0,-1), Point(1,0), Point(-1,0))
            .forEach { pos ->
                    val thisData = data[position + pos]
                    // check for empty positions or keys
                    if (thisData != null &&
                        (thisData.vaultItem == VaultItem.EMPTY || thisData.vaultItem == VaultItem.KEY))
                        neighbours.add(GraphNode(
                             GraphKey(position + pos, mutableSetOf<Char>().also { it.addAll(id.keys) })
                        ) { p -> getNeighbours(p) })
                    // check for gates
                    if (thisData != null &&
                        thisData.vaultItem == VaultItem.GATE &&
                        id.keys.contains(thisData.value.lowercaseChar()))
                        neighbours.add(GraphNode(
                            GraphKey(position + pos, mutableSetOf<Char>().also { it.addAll(id.keys) })
                        ) { p -> getNeighbours(p) })
            }
        println("neighbours of $id: ${neighbours.map { it.nodeId }}")
        return neighbours
    }

    fun updateGraphIdWithKey(id: GraphKey): GraphKey {
        val updatedId = id.copy()
        if (data[id.position]!!.vaultItem == VaultItem.KEY)
            updatedId.keys.add(data[id.position]!!.value)
        updatedId.keys = updatedId.keys.sorted().toMutableSet()
        return updatedId
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

    data class GraphKey(var position: Point, var keys: MutableSet<Char>) {
        override fun equals(other: Any?): Boolean {
            if (other == null || other !is GraphKey)
                return false
            if (this.position == END_POINT || other.position == END_POINT)
                return this.keys == other.keys
            return super.equals(other)
        }

        override fun hashCode(): Int {
            var result = position.hashCode()
            result = 31 * result + keys.hashCode()
            return result
        }

        fun copy() = GraphKey(Point(position.x, position.y), mutableSetOf<Char>().also { it.addAll(keys) })
    }
}

