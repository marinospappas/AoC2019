package mpdev.springboot.aoc2019.solutions.day13

import java.awt.Point
import mpdev.springboot.aoc2019.solutions.day13.Tile.*

class ArcadeGame {

    private var board: MutableMap<Point, Tile> = mutableMapOf()
    var score: Long = 0L
    private var curBallPosition = Point(-1,-1)
    private var prevBallPosition = Point(-1,-1)
    private var paddlePosition = Point(-1,-1)

    lateinit var inputData: List<Int>

    fun receiveInput(inputData: List<Int>) {
        //println("received: $inputData")
        this.inputData = inputData
    }

    private var firstMove = true

    fun getJoystick(): Int {
        //TODO: algorithm to decide joystick tilt based on ball movement
        return if (firstMove) 0 else 1
    }

    fun over() = curBallPosition.y > paddlePosition.y

    fun updateBoard() {
        for (i in inputData.indices step(3)) {
            val position = Point(inputData[i], inputData[i + 1])
            val tile = Tile.fromInt(inputData[i + 2])
            if (position == Point(-1,0))
                score = inputData[i + 2].toLong()
            else
                board[position] = tile
            if (tile == BALL) {
                prevBallPosition = Point(curBallPosition.x, curBallPosition.y)
                curBallPosition = Point(position.x, position.y)
            }
            if (tile == HPADDLE) {
                paddlePosition = Point(position.x, position.y)
            }
        }
    }

    private fun board2Grid(board: Map<Point, Tile>): Array<CharArray> {
        val maxX = board.keys.maxOf { it.x } + 1
        val maxY = board.keys.maxOf { it.y } + 1
        val grid: Array<CharArray> = Array(maxY) { CharArray(maxX) { EMPTY.ascii } }
        board.forEach { (pos, tile) -> grid[pos.y][pos.x] = tile.ascii }
        return grid
    }

    fun getNumberOfBlocks() = board.values.count { tile -> tile == BLOCK }

    fun printBoard() {
        val grid = board2Grid(board)
        for (i in grid.indices) {
            for (j in grid.first().indices)
                print(grid[i][j])
            println()
        }
        println("Score: $score")
        //println("Ball previous: $prevBallPosition current: $curBallPosition")
        //println("Paddle: $paddlePosition")
    }
}