package mpdev.springboot.aoc2019.solutions.day16

import mpdev.springboot.aoc2019.model.PuzzlePartSolution
import mpdev.springboot.aoc2019.solutions.PuzzleSolver
import org.springframework.stereotype.Component
import kotlin.system.measureTimeMillis

@Component
class Day16: PuzzleSolver() {

    final override fun setDay() {
        day = 16         ////// update this when a puzzle solver for a new day is implemented
    }

    init {
        setDay()
    }

    lateinit var fft: Fft
    var result = 0L

    override fun initSolver() {
        fft = Fft(inputData[0])
    }

    override fun solvePart1(): PuzzlePartSolution {
        val elapsed = measureTimeMillis {
            repeat(100) { fft.transform() }
            result = fft.signal.toList().joinToString("").substring(0,8).toLong()
        }
        return PuzzlePartSolution(1, result.toString(), elapsed)
    }

    override fun solvePart2(): PuzzlePartSolution {
        val elapsed = measureTimeMillis {
        }
        return PuzzlePartSolution(2, result.toString(), elapsed)
    }

}
