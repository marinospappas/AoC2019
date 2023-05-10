package mpdev.springboot.aoc2019.solutions.day11

import mpdev.springboot.aoc2019.solutions.day13.Tile
import mpdev.springboot.aoc2019.utils.AocException
import mpdev.springboot.aoc2019.utils.plus
import java.awt.Point

class RobotController {

    private val BLACK = 0
    private val WHITE = 1

    private val LEFT = 0
    private val RIGHT = 1

    private val directions = listOf(
        Point(0,1), Point(1,0), Point(0,-1), Point(-1,0)    // up, right, down, left
    )

    private var robotPosition = Point(0,0)     // starting at position 0,0
    private var directionOfMovement = 0             // facing up
    private val panels = mutableMapOf<Point,Int>().also { p -> p[robotPosition] = BLACK }

    fun initPanels(part: Int = 1) {
        directionOfMovement = 0             // facing up
        panels.clear()
        panels[robotPosition] = if (part == 1) BLACK else WHITE
    }

    fun getInputForRobot() = panels[robotPosition] ?: BLACK

    fun receiveRobotOutput(outputValues: List<Int>) {
        panels[robotPosition] = outputValues[0]
        updateDirection(outputValues[1])
        moveRobot()
    }

    fun countPanels() = panels.count()

    private fun updateDirection(turn: Int) {
        when (turn) {
            LEFT -> --directionOfMovement
            RIGHT -> ++directionOfMovement
            else -> throw AocException("invalid direction of turn $turn")
        }
        if (directionOfMovement < 0)
            directionOfMovement = directions.lastIndex
        if (directionOfMovement > directions.lastIndex)
            directionOfMovement = 0
    }

    private fun moveRobot() {
        robotPosition += directions[directionOfMovement]
    }

    private fun panels2Grid(panels: Map<Point,Int>): Array<CharArray> {
        val maxX = panels.keys.maxOf { it.x } + 1
        val maxY = panels.keys.maxOf { it.y } + 1
        val grid: Array<CharArray> = Array(maxY) { CharArray(maxX) { Tile.EMPTY.ascii } }
        panels.forEach { (pos, color) -> grid[pos.y][pos.x] = if (color == BLACK) '.' else 'O' }
        return grid
    }

    fun printGrid() {
        val grid = panels2Grid(panels)
        for (i in grid.lastIndex downTo 0) {
            for (j in grid.first().indices)
                print(grid[i][j])
            println()
        }
        println()
    }
}