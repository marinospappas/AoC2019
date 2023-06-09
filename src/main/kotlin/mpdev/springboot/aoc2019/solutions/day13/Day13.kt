package mpdev.springboot.aoc2019.solutions.day13

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
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
        var elapsed: Long
        runBlocking {
            elapsed = measureTimeMillis {
                val job = launch { icvm.runProgram() }
                icvm.waitProgram(job)
                setupGame(icvm.getProgramOutput())
            }
        }
        result = game.getNumberOfBlocks()
        return PuzzlePartSolution(1, result.toString(), elapsed)
    }

    override fun solvePart2(): PuzzlePartSolution {
        log.info("solving day $day part 2")
        val icvm = ICVM(inputData[0])
        icvm.setProgramMemory(0, 2)
        var elapsed: Long
        runBlocking {
            elapsed = measureTimeMillis {
                val job = launch { icvm.runProgram() }
                setupGame(icvm.getProgramOutput())
                game.printBoard()
                log.info("Please be patient, {} blocks to deal with... this will take time", game.getNumberOfBlocks())
                playGame(icvm)
                icvm.waitProgram(job)
            }
        }
        val result: String = if (game.over()) "Game Over!!!" else game.score.toString()
        return PuzzlePartSolution(2, result, elapsed)
    }

    fun setupGame(data: List<Int>) {
        game = ArcadeGame()
        game.receiveInput(data.toList())
    }

    suspend fun playGame(icvm: ICVM) {
        var prevScore = 0L
        while (true) {
            val joystick = game.getJoystick()
            log.debug("joystick movement {}", joystick)
            icvm.setProgramInput(joystick)
            val output = icvm.getProgramOutput()
            if (output.isNotEmpty())
                game.receiveInput(output)
            if (!icvm.programIsRunning())
                break
            if (game.getNumberOfBlocks() % 50 == 0 && game.score > prevScore)
                log.info("Current score: {}, blocks remaining: {}", game.score, game.getNumberOfBlocks())
            prevScore = game.score
        }
        game.printBoard()
    }
}