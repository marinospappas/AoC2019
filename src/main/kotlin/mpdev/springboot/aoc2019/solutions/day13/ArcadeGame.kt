package mpdev.springboot.aoc2019.solutions.day13

import java.awt.Point
import mpdev.springboot.aoc2019.solutions.day13.Tile.*

class ArcadeGame {

    private var board: MutableMap<Point, Tile> = mutableMapOf()
    var score: Long = 0L
    private var curBallPosition = Point(-1,-1)
    private var prevBallPosition = Point(-1,-1)
    private var paddlePosition = Point(-1,-1)

    var maxX = 0
    var maxY = 0

    private var firstRound = true

    fun receiveInput(inputData: List<Int>) {
        println("received: $inputData")
        updateBoard(inputData)
        if (firstRound) {
            maxX = board.keys.maxOf { it.x } + 1
            maxY = board.keys.maxOf { it.y } + 1
            printBoard()
        }
        firstRound = false
    }

    private var paddleDirection = 0

    fun getJoystick(): List<Int> {
        // follow the ball
        val tilt = when  {
            curBallPosition.x > paddlePosition.x -> 1
            curBallPosition.x < paddlePosition.x -> -1
            curBallPosition.x == paddlePosition.x && paddlePosition.y - curBallPosition.y > 1 -> paddleDirection
            else -> 0
        }
        paddleDirection = tilt
        return listOf(tilt)
        /*
        if (prevBallPosition == Point(-1,-1))   // first move - paddle stays still
            return listOf(0).also { paddleDirection = 0 }
        val ballPredictedX = predictBallXPosition()
        println("predicted ball x = $ballPredictedX")
        if (ballPredictedX < 0)                     // ball going up - paddle stays still
            return listOf(0).also { paddleDirection = 0 }
        if (ballPredictedX == paddlePosition.x)     // ball will hit the paddle - paddle stays still
            return listOf(0).also { paddleDirection = 0 }
        if (paddleDirection == 0 && ballPredictedX < paddlePosition.x)      // ball will go left of the paddle - paddle goes left
            return listOf(-1).also { paddleDirection = -1 }
        if (paddleDirection == -1 && ballPredictedX < paddlePosition.x-1)      // ball will go left of the paddle - paddle goes left
            return listOf(-1).also { paddleDirection = -1 }
        if (paddleDirection == 0 && ballPredictedX > paddlePosition.x)      // ball will go right of the paddle - paddle goes right
            return listOf(1).also { paddleDirection = 1 }
        if (paddleDirection == 1 && ballPredictedX > paddlePosition.x+1)      // ball will go right of the paddle - paddle goes right
            return listOf(1).also { paddleDirection = 1 }
        return listOf(0).also { paddleDirection = 0 }
         */
        //TODO: complete the algorithm to decide joystick tilt based on ball movement
    }

    private fun predictBallXPosition(): Int {
        if (curBallPosition.y < prevBallPosition.y) // ball goes up - return 0 or maxX to indicate direction of movement
            return -1 //if (curBallPosition.x < prevBallPosition.x) 0 else 40
        // TODO: take ball bouncing into account
        // below is based on the ball going straight towards the bottom (no bouncing)
        return prevBallPosition.x + (curBallPosition.x - prevBallPosition.x) * (paddlePosition.y - prevBallPosition.y - 1)
    }

    fun over() = curBallPosition.y > paddlePosition.y

    private fun updateBoard(inputData: List<Int>) {
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
        val grid: Array<CharArray> = Array(maxY) { CharArray(maxX) { EMPTY.ascii } }
        board.forEach { (pos, tile) -> grid[pos.y][pos.x] = tile.ascii }
        return grid
    }

    fun getNumberOfBlocks() = board.values.count { tile -> tile == BLOCK }

    fun printBoard() {
        val grid = board2Grid(board)
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
        println("Score: $score")
        println("Ball previous: $prevBallPosition current: $curBallPosition")
        println("Paddle: $paddlePosition")
        println()
    }
}