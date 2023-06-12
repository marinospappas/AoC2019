package mpdev.springboot.aoc2019.solutions.day08

import mpdev.springboot.aoc2019.model.PuzzlePartSolution
import mpdev.springboot.aoc2019.solutions.PuzzleSolver
import org.springframework.stereotype.Component
import kotlin.system.measureTimeMillis

@Component
class Day08: PuzzleSolver() {

    final override fun setDay() {
        day = 8         ////// update this when a puzzle solver for a new day is implemented
    }

    init {
        setDay()
    }


    lateinit var image: Image
    var result = 0

    override fun initSolver() {
        image = Image(inputData[0])
    }

    override fun solvePart1(): PuzzlePartSolution {
        result = 0
        val elapsed = measureTimeMillis {
            val layer0 = image.layers.minByOrNull { it.countDigit(0) }!!
            result = layer0.countDigit(1) * layer0.countDigit(2)
        }
        return PuzzlePartSolution(1, result.toString(), elapsed)
    }

    override fun solvePart2(): PuzzlePartSolution {
        result = 0
        val elapsed = measureTimeMillis {
            image.decode().print()
        }
        return PuzzlePartSolution(2, "Result in Logfile", elapsed)
    }

}