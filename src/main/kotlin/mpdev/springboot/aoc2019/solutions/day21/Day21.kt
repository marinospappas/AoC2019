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

    private val inputStringsPart2 = arrayOf(
        "OR A T\n",
        "AND B T\n",
        "AND C T\n",
        "NOT T J\n",
        "AND D J\n",
        "NOT J T\n",
        "OR E T\n",
        "OR H T\n",
        "AND T J\n",
        "RUN\n"
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
        val icvm = ICVM(inputData[0])
        icvm.setAsciiCapable()
        var elapsed: Long
        runBlocking {
            elapsed = measureTimeMillis {
                val job = launch { icvm.runProgram() }
                inputStringsPart2.forEach { s -> icvm.setInputValuesAscii(s) }
                icvm.waitProgram(job)
                result = icvm.getOutputValues().last()
            }
        }
        return PuzzlePartSolution(1, result.toString(), elapsed)
    }

}