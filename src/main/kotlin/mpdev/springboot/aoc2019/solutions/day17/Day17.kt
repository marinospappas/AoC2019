package mpdev.springboot.aoc2019.solutions.day17

import mpdev.springboot.aoc2019.model.PuzzlePartSolution
import mpdev.springboot.aoc2019.solutions.PuzzleSolver
import mpdev.springboot.aoc2019.solutions.icvm.ICVM
import mpdev.springboot.aoc2019.solutions.icvm.InputOutput.getOutputValues
import mpdev.springboot.aoc2019.solutions.icvm.InputOutput.getOutputValuesAscii
import mpdev.springboot.aoc2019.solutions.icvm.InputOutput.setInputValuesAscii
import org.springframework.stereotype.Component
import kotlin.system.measureTimeMillis

@Component
class Day17: PuzzleSolver() {

    final override fun setDay() {
        day = 17         ////// update this when a puzzle solver for a new day is implemented
    }

    init {
        setDay()
    }

    var result = 0

    private val inputStringsPart2 = arrayOf(
        "A,B,B,A,B,C,A,C,B,C\n",
        "L,4,L,6,L,8,L,12\n",
        "L,8,R,12,L,12\n",
        "R,12,L,6,L,6,L,8\n",
        "n\n"
    )

    override fun initSolver() {}

    override fun solvePart1(): PuzzlePartSolution {
        log.info("solving day $day part 1")
        val icvm = ICVM(inputData[0])
        val elapsed = measureTimeMillis {
            icvm.runProgram()
            icvm.waitProgram()
            val output = getOutputValuesAscii().trim('\n')
            val asciiProcessor = AsciiProcessor(output)
            result = asciiProcessor.process()
        }
        return PuzzlePartSolution(1, result.toString(), elapsed)
    }

    override fun solvePart2(): PuzzlePartSolution {
        log.info("solving day $day part 2")
        result = 0
        val icvm = ICVM(inputData[0])
        icvm.setProgramMemory(0, 2)
        inputStringsPart2.forEach { s -> setInputValuesAscii(s) }
        val elapsed = measureTimeMillis {
            icvm.runProgram()
            icvm.waitProgram()
            result = getOutputValues(clearChannel = false).last().toInt()
            print(getOutputValuesAscii())
        }
        return PuzzlePartSolution(2, result.toString(), elapsed)
    }

}