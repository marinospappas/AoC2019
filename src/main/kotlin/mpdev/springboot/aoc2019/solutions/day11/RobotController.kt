package mpdev.springboot.aoc2019.solutions.day11

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
    private var panels = mutableMapOf<Point,Int>().also { p -> p[robotPosition] = BLACK }

    fun initPanels(part: Int = 1) {
        directionOfMovement = 0             // facing up
        panels = mutableMapOf()
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
        val maxX = panels.keys.maxOf { it.x }
        val maxY = panels.keys.maxOf { it.y }
        val minX = panels.keys.minOf { it.x }
        val minY = panels.keys.minOf { it.y }
        val grid: Array<CharArray> = Array(maxY - minY + 1) { CharArray(maxX - minX + 1) { ' ' } }
        panels.forEach { (pos, color) -> grid[pos.y - minY][pos.x - minX] = if (color == BLACK) ' ' else '█' }
        return grid
    }

    fun printGrid() {
        Thread.sleep(10)
        val grid = panels2Grid(panels)
        for (i in grid.lastIndex downTo 0) {
            for (j in grid.first().indices)
                print(grid[i][j])
            println()
        }
        println()
    }
}