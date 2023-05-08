package mpdev.springboot.aoc2019.solutions.day13

import java.awt.Point
import mpdev.springboot.aoc2019.solutions.day13.Tile.*
class ArcadeGame(inputData: List<Int>) {

    var board: MutableMap<Point, Tile> = mutableMapOf()

    init {
        for (i in inputData.indices step(3))
            board[Point(inputData[i],inputData[i+1])] = Tile.fromInt(inputData[i+2])
    }

    private fun board2Grid(board: Map<Point, Tile>): Array<CharArray> {
        val maxX = board.keys.maxOf { it.x } + 1
        val maxY = board.keys.maxOf { it.y } + 1
        val grid: Array<CharArray> = Array(maxY) { CharArray(maxX) { EMPTY.ascii } }
        board.forEach { (pos, tile) -> grid[pos.y][pos.x] = tile.ascii }
        return grid
    }

    fun printBoard() {
        val grid = board2Grid(board)
        for (element in grid) {
            for (i in 0 until grid.first().size)
                print(element[i])
            println()
        }
    }
}