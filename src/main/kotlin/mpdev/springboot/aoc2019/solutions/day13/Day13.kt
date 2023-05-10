package mpdev.springboot.aoc2019.solutions.day13

import mpdev.springboot.aoc2019.model.PuzzlePartSolution
import mpdev.springboot.aoc2019.solutions.PuzzleSolver
import mpdev.springboot.aoc2019.solutions.icvm.ICVM
import org.springframework.stereotype.Component
import kotlin.system.measureTimeMillis

@Component
class Day13: PuzzleSolver() {

    final override fun setDay() {
        day = 13         ////// update this when a puzzle solver for a new day is implemented
    }

    init {
        setDay()
    }

    var result = 0
    override fun initSolver() {}
    private lateinit var game: ArcadeGame

    override fun solvePart1(): PuzzlePartSolution {
        log.info("solving day $day part 1")
        val icvm = ICVM(inputData[0])
        val elapsed = measureTimeMillis {
            icvm.runProgram()
            icvm.waitProgram()
            setupGame(icvm.getProgramOutput())
            result = game.getNumberOfBlocks()
        }
        return PuzzlePartSolution(1, result.toString(), elapsed)
    }

    override fun solvePart2(): PuzzlePartSolution {
        log.info("solving day $day part 2")
        var result: String
        val icvm = ICVM(inputData[0])
        icvm.setProgramMemory(0, 2)
        val elapsed = measureTimeMillis {
            icvm.runProgram()
            setupGame(icvm.getProgramOutput())
            game.printBoard()
            playGame(icvm)
            icvm.waitProgram()
            result = if (game.over()) "Game Over!!!" else game.score.toString()
        }
        return PuzzlePartSolution(2, result, elapsed)
    }

    fun setupGame(data: List<Int>) {
        game = ArcadeGame()
        game.receiveInput(data.toList())
    }

    fun playGame(icvm: ICVM) {
        while (true) {
            val joystick = game.getJoystick()
            //println("joystick movement $joystick")
            icvm.setProgramInput(joystick)
            val output = icvm.getProgramOutput()
            if (output.isNotEmpty())
                game.receiveInput(output)
            if (!icvm.programIsRunning())
                break
            //game.printBoard()
        }
        game.printBoard()
    }
}