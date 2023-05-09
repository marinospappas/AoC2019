package mpdev.springboot.aoc2019.solutions.day13

import mpdev.springboot.aoc2019.model.PuzzlePartSolution
import mpdev.springboot.aoc2019.solutions.PuzzleSolver
import mpdev.springboot.aoc2019.solutions.icvm.InputOutput.initInputOutput
import mpdev.springboot.aoc2019.solutions.icvm.InputOutput.getOutputValues
import mpdev.springboot.aoc2019.solutions.icvm.ICProgram
import mpdev.springboot.aoc2019.solutions.icvm.InputOutput.setInputValues
import org.springframework.stereotype.Component
import kotlin.concurrent.thread
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
        val outputValues = mutableListOf<Long>()
        initInputOutput()
        val program = ICProgram(inputData[0])
        val elapsed = measureTimeMillis {
            val t1 = thread(start = true, name = "arcade-game-0") {    // when input/output is required the intCode must run in a separate thread
                program.run()
            }
            val t2 = thread(start = true, name = "output-thread-0") {   // thread that collects the output - exits when non-ascii char received
                try {
                    while (true) {
                        outputValues.addAll(getOutputValues())
                    }
                } catch (e: InterruptedException) {
                    log.info("output thread exiting")
                }
            }
            t1.join()
            t2.interrupt()
            game = ArcadeGame()
            sendOutputToGame(outputValues)
            game.updateBoard()
            game.printBoard()
            result = game.getNumberOfBlocks()
        }
        return PuzzlePartSolution(1, result.toString(), elapsed)
    }

    override fun solvePart2(): PuzzlePartSolution {
        log.info("solving day $day part 2")
        val outputValues = mutableListOf<Long>()
        var result = ""
        initInputOutput()
        val program = ICProgram(inputData[0])
        program.setMemory(0, 2)
        val elapsed = measureTimeMillis {
            val t1 = thread(start = true, name = "arcade-game-0") {    // when input/output is required the intCode must run in a separate thread
                program.run()
            }
            val t2 = thread(start = true, name = "output-thread-0") {  // thread that collects the output - exits when non-ascii char received
                try {
                    while (true) {
                        outputValues.addAll(getOutputValues())
                    }
                } catch (e: InterruptedException) {
                    log.info("output thread exiting")
                }
            }
            game = ArcadeGame()
            sendOutputToGame(outputValues)
            game.updateBoard()
            game.printBoard()
            while (true) {
                if (game.getNumberOfBlocks() == 0 || game.over())
                    break
                setInputValues(listOf(game.getJoystick().toLong()))
                sendOutputToGame(outputValues)
                game.updateBoard()
                //game.printBoard()
            }
            t1.join()
            t2.interrupt()
            game.printBoard()
            result = if (game.score == 0L) "Game Over!!!" else game.score.toString()
        }
        return PuzzlePartSolution(2, result, elapsed)
    }

    fun sendOutputToGame(output: MutableList<Long>) {
        var prevOutputSize = output.size
        while (true) {
            Thread.sleep(3)
            val curOutputSize = output.size
            if (curOutputSize > 0 && curOutputSize == prevOutputSize)
                break
            prevOutputSize = curOutputSize
        }
        game.receiveInput(output.toList().map { it.toInt() })
        output.clear()
    }
}