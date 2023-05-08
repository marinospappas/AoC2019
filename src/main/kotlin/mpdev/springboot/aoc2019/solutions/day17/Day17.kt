package mpdev.springboot.aoc2019.solutions.day17

import mpdev.springboot.aoc2019.model.PuzzlePartSolution
import mpdev.springboot.aoc2019.solutions.PuzzleSolver
import mpdev.springboot.aoc2019.solutions.icvm.InputOutput.initInputOutput
import mpdev.springboot.aoc2019.solutions.icvm.InputOutput.getOutputValues
import mpdev.springboot.aoc2019.solutions.icvm.InputOutput.getOutputValuesAscii
import mpdev.springboot.aoc2019.solutions.icvm.InputOutput.setInputValuesAscii
import mpdev.springboot.aoc2019.solutions.icvm.Program
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import kotlin.concurrent.thread
import kotlin.system.measureTimeMillis

@Component
class Day17: PuzzleSolver() {

    companion object {
        private val log: Logger = LoggerFactory.getLogger(this::class.java)
    }

    final override fun setDay() {
        day = 17         ////// update this when a puzzle solver for a new day is implemented
    }

    init {
        setDay()
    }

    var result = 0

    private val inputStrings = arrayOf(
        "A,B,B,A,B,C,A,C,B,C\n",
        "L,4,L,6,L,8,L,12\n",
        "L,8,R,12,L,12\n",
        "R,12,L,6,L,6,L,8\n",
        "n\n"
    )

    override fun initSolver() {}

    override fun solvePart1(): PuzzlePartSolution {
        log.info("solving day 17 part 1")
        initInputOutput()
        val program = Program(inputData[0])
        val elapsed = measureTimeMillis {
            thread(start = true, name = "self-test-0") {    // when input/output is required the intCode must run in a separate thread
                program.run()
            }.join()
            val output = getOutputValuesAscii(clearChannel = false).trim('\n')
            val asciiProcessor = AsciiProcessor(output)
            result = asciiProcessor.process()
        }
        return PuzzlePartSolution(1, result.toString(), elapsed)
    }

    override fun solvePart2(): PuzzlePartSolution {
        log.info("solving day 17 part 2")
        initInputOutput()
        val program = Program(inputData[0])
        program.setMemory(0, 2)
        inputStrings.forEach { s -> setInputValuesAscii(s) }
        val elapsed = measureTimeMillis {
            thread(start = true, name = "self-test-0") {    // when input/output is required the intCode must run in a separate thread
                program.run()
            }.join()
        }
        result = getOutputValues(clearChannel = false).last().toInt()
        println(getOutputValuesAscii(clearChannel = false))
        return PuzzlePartSolution(2, result.toString(), elapsed)
    }

}