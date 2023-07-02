package mpdev.springboot.aoc2019.solutions.day20

import java.awt.Point
import mpdev.springboot.aoc2019.utils.Graph
import mpdev.springboot.aoc2019.utils.GraphNode
import mpdev.springboot.aoc2019.utils.plus

class Maze(val input: List<String>) {

    var data: MutableMap<Point, MazePoint> = mutableMapOf()
    private lateinit var graphP1: Graph<Point>

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

    fun createGraph() {
        graphP1 = Graph { p -> getNeighboursP1(p)}
        data.forEach { (k,_) -> graphP1.addNode(k) }
    }

    fun getStartP1(): GraphNode<Point> = GraphNode(start){ p -> getNeighboursP1(p)}

    fun getEndP1(): GraphNode<Point> = GraphNode(end){ p -> getNeighboursP1(p)}

    private fun getNeighboursP1(position: Point): List<GraphNode<Point>> {
        val neighbours = mutableListOf<GraphNode<Point>>()
        setOf(Point(0,1), Point(0,-1), Point(1,0), Point(-1,0))
            .forEach { pos ->
                    val thisData = data[position + pos]
                    if (thisData != null &&
                        (thisData.mazeItem == MazeItem.EMPTY
                                || thisData.mazeItem == MazeItem.CONNECTION
                                || thisData.mazeItem == MazeItem.END))
                        neighbours.add(GraphNode(position + pos) { p -> getNeighboursP1(p) })
            }
        if (data[position]!!.mazeItem == MazeItem.CONNECTION) {
            val connectedPt = data.entries
                .filter { e -> e.key != position && e.value.value == data[position]!!.value }
                .first()
                .key
            neighbours.add(GraphNode(connectedPt){ p -> getNeighboursP1(p)})
        }
        return neighbours
    }

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