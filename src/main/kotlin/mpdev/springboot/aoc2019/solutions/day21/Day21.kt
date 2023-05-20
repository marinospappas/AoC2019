package mpdev.springboot.aoc2019.solutions.day21

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import mpdev.springboot.aoc2019.model.PuzzlePartSolution
import mpdev.springboot.aoc2019.solutions.PuzzleSolver
import mpdev.springboot.aoc2019.solutions.icvm.ICVM
import org.springframework.stereotype.Component
import kotlin.system.measureTimeMillis

@Component
class Day21: PuzzleSolver() {

    final override fun setDay() {
        day = 21         ////// update this when a puzzle solver for a new day is implemented
    }

    init {
        setDay()
    }

    var result = 0L

    private val inputStringsPart1 = arrayOf(
        "OR A T\n",
        "AND B T\n",
        "AND C T\n",
        "NOT T J\n",
        "AND D J\n",
        "WALK\n"
    )

    override fun initSolver() {}

    override fun solvePart1(): PuzzlePartSolution {
        log.info("solving day $day part 1")
        val icvm = ICVM(inputData[0])
        icvm.setAsciiCapable()
        var elapsed: Long
        runBlocking {
            elapsed = measureTimeMillis {
                val job = launch { icvm.runProgram() }
                inputStringsPart1.forEach { s -> icvm.setInputValuesAscii(s) }
                icvm.waitProgram(job)
                result = icvm.getOutputValues().last()
            }
        }
        return PuzzlePartSolution(1, result.toString(), elapsed)
    }

    override fun solvePart2(): PuzzlePartSolution {
        log.info("solving day $day part 2")
        result = 0
        val icvm = ICVM(inputData[0])
        icvm.setProgramMemory(0, 2)
        var elapsed: Long
        runBlocking {
            elapsed = measureTimeMillis {
            /*
                inputStringsPart2.forEach { s -> icvm.setInputValuesAscii(s) }
                val job = launch { icvm.runProgram() }
                icvm.waitProgram(job)
                val output = icvm.getOutputValues()
                result = output.last().toInt()
                output.forEach { print(it.toInt().toChar()) }
                */
            }
        }
        return PuzzlePartSolution(2, result.toString(), elapsed)
    }

}