package mpdev.springboot.aoc2019.solutions.day13

import mpdev.springboot.aoc2019.model.PuzzlePartSolution
import mpdev.springboot.aoc2019.solutions.PuzzleSolver
import mpdev.springboot.aoc2019.solutions.icvm.InputOutput.initInputOutput
import mpdev.springboot.aoc2019.solutions.icvm.InputOutput.setInputValues
import mpdev.springboot.aoc2019.solutions.icvm.InputOutput.getOutputValues
import mpdev.springboot.aoc2019.solutions.icvm.ICProgram
import mpdev.springboot.aoc2019.solutions.day13.Tile.*
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

    private lateinit var arcadeGame: ArcadeGame

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
            arcadeGame = ArcadeGame(outputValues.map { it.toInt() })
            arcadeGame.printBoard()
            result = arcadeGame.board.values.count { tile -> tile == BLOCK }
        }
        return PuzzlePartSolution(1, result.toString(), elapsed)
    }

    override fun solvePart2(): PuzzlePartSolution {
        log.info("solving day $day part 2")
        val outputValues = mutableListOf<Long>()
        result = 0
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
                        log.info("output: {}", outputValues)
                    }
                } catch (e: InterruptedException) {
                    log.info("output thread exiting")
                }
            }
            arcadeGame = ArcadeGame(outputValues.map { it.toInt() })
            println(arcadeGame.board)
            arcadeGame.printBoard()
            setInputValues(listOf(1L))
            setInputValues(listOf(1L))
            setInputValues(listOf(1L))
            setInputValues(listOf(1L))
            setInputValues(listOf(1L))
            t1.join()
            t2.interrupt()
        }
        return PuzzlePartSolution(2, result.toString(), elapsed)
    }
}