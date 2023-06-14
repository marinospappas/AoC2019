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
        fft.initPatterns()
    }

    override fun solvePart1(): PuzzlePartSolution {
        val elapsed = measureTimeMillis {
            repeat(100) { fft.transform() }
            result = fft.signal.toList().joinToString("").take(8).toLong()
        }
        return PuzzlePartSolution(1, result.toString(), elapsed)
    }

    override fun solvePart2(): PuzzlePartSolution {
        val repeatFactor = 10000
        val offset = inputData[0].take(7).toInt()
        val inputLength = inputData[0].length
        val offsetInInput = offset % inputLength
        var input = inputData[0].substring(offsetInInput)
        (0 until (repeatFactor * inputLength - offset) / inputLength).forEach { _ -> input += inputData[0] }
        fft = Fft(input)
        val elapsed = measureTimeMillis {
            repeat(100) { fft.fastTransform() }
            result = fft.signal.toList().joinToString("").take(8).toLong()
        }
        return PuzzlePartSolution(2, result.toString(), elapsed)
    }

}
