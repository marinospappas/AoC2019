package mpdev.springboot.aoc2019.solutions.day20

import mpdev.springboot.aoc2019.utils.AocException
import java.awt.Point
import mpdev.springboot.aoc2019.utils.Graph
import mpdev.springboot.aoc2019.utils.GraphNode
import mpdev.springboot.aoc2019.utils.plus

class Maze(val input: List<String>) {

    var data: MutableMap<Point, MazePoint> = mutableMapOf()

    var maxX = 0
    var maxY = 0
    private var start = Point(0,0)
    private var end = Point(0,0)

    init {
        input.indices.forEach { y ->
            input[y].indices.forEach { x ->
                if (input[y][x] != ' ') {
                    data[Point(x, y)] = when (input[y][x]) {
                        '.' -> MazePoint(MazeItem.EMPTY, input[y][x])
                        '#' -> MazePoint(MazeItem.WALL, input[y][x])
                        '0' -> {
                            start = Point(x, y)
                            MazePoint(MazeItem.START, input[y][x])
                        }
                        '9' -> {
                            end = Point(x, y)
                            MazePoint(MazeItem.END, input[y][x])
                        }
                        else -> MazePoint(MazeItem.CONNECTION, input[y][x])
                    }
                }
            }
        }
        maxX = input.first().length
        maxY = input.size
    }

    ///////// part 1
    private lateinit var graphP1: Graph<Point>

    fun createGraphP1() {
        graphP1 = Graph { p -> getNeighboursP1(p) }
        data.forEach { (k,_) -> graphP1.addNode(k) }
    }

    fun getStartP1(): GraphNode<Point> = GraphNode(start){ p -> getNeighboursP1(p)}
    fun getEndP1(): GraphNode<Point> = GraphNode(end){ p -> getNeighboursP1(p)}

    private fun getNeighboursP1(position: Point): List<GraphNode<Point>> {
        val neighbours = mutableListOf<GraphNode<Point>>()
        // get neighbours in the surrounding area
        setOf(Point(0,1), Point(0,-1), Point(1,0), Point(-1,0))
            .forEach { pos ->
                    val thisData = data[position + pos]
                    if (thisData != null &&
                        (thisData.mazeItem == MazeItem.EMPTY
                                || thisData.mazeItem == MazeItem.CONNECTION
                                || thisData.mazeItem == MazeItem.END))
                        neighbours.add(GraphNode(position + pos) { p -> getNeighboursP1(p) })
            }
        // check for neighbours via a connection
        if (data[position]!!.mazeItem == MazeItem.CONNECTION) {
            val connectedPt = data.entries.first { e -> e.key != position && e.value.value == data[position]!!.value }
                .key
            neighbours.add(GraphNode(connectedPt){ p -> getNeighboursP1(p)})
        }
        return neighbours
    }

    ///////// part 2
    var depthLimit = Int.MAX_VALUE
    private lateinit var graphP2: Graph<Pair<Int,Point>>

    fun createGraphP2() {
        graphP2 = Graph { p -> getNeighboursP2(p)}
        data.forEach { (k,_) -> graphP2.addNode(Pair(0,k)) }
    }

    // part 2 starts and ends at level 0
    fun getStartP2(): GraphNode<Pair<Int,Point>> = GraphNode(Pair(0, start)){ p -> getNeighboursP2(p)}
    fun getEndP2(): GraphNode<Pair<Int,Point>> = GraphNode(Pair(0, end)){ p -> getNeighboursP2(p)}

    private fun getNeighboursP2(position: Pair<Int,Point>): List<GraphNode<Pair<Int,Point>>> {
        val neighbours = mutableListOf<GraphNode<Pair<Int,Point>>>()
        if (position.first > depthLimit)
            return neighbours
        // get neighbours in the surrounding area (this level)
        setOf(Point(0,1), Point(0,-1), Point(1,0), Point(-1,0))
            .forEach { posIncr ->
                val newPos = position.second + posIncr
                val thisData = data[newPos]
                if (thisData != null &&
                    (thisData.mazeItem == MazeItem.EMPTY
                            || (thisData.mazeItem == MazeItem.CONNECTION && (position.first > 0 || !pointInPerimeter(newPos)))
                            || (thisData.mazeItem == MazeItem.END && position.first == 0)))
                    neighbours.add(GraphNode(Pair(position.first, newPos)) { p -> getNeighboursP2(p) })
            }
        // check for neighbours via a connection (different level)
        if (data[position.second]!!.mazeItem == MazeItem.CONNECTION) {
            val connectedPos = data.entries.first {
                    e -> e.value.value == data[position.second]!!.value && e.key != position.second
                }.key
            val connectedLevelIncr = if (pointInPerimeter(position.second)) -1 else +1
            val connectedPt = Pair(position.first + connectedLevelIncr, connectedPos)
            neighbours.add(GraphNode(connectedPt){ p -> getNeighboursP2(p)})
            if (position.first + connectedLevelIncr < 0) {
                println("neighbours of $position: ${neighbours.map { it.nodeId } }")
                throw AocException("negative level")
            }
        }
        return neighbours
    }

    private fun pointInPerimeter(p: Point) = setOf(0, maxX-1).contains(p.x) || setOf(0, maxY-1).contains(p.y)

    private fun maze2Grid(maze: Map<Point, MazePoint>): Array<CharArray> {
        val grid: Array<CharArray> = Array(maxY) { CharArray(maxX) { ' ' } }
        maze.forEach { (pos, item) -> grid[pos.y][pos.x] = item.value }
        return grid
    }

    fun printMaze() {
        val grid = maze2Grid(data)
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
}