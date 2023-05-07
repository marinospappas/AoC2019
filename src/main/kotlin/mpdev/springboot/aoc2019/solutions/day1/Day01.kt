package mpdev.springboot.aoc2019.solutions.day1

import mpdev.springboot.aoc2019.model.PuzzlePartSolution
import mpdev.springboot.aoc2019.solutions.PuzzleSolver
import org.springframework.stereotype.Component
import kotlin.system.measureTimeMillis

@Component
class Day01: PuzzleSolver() {

    final override fun setDay() {
        day = 1
    }

    init {
        setDay()
    }

    var result = 0L

    override fun initSolver() {}

    override fun solvePart1(): PuzzlePartSolution {
        val elapsed = measureTimeMillis {
            result = inputData.map { data -> data.toLong() / 3 - 2 }.sum()
        }
        return PuzzlePartSolution(1, result.toString(), elapsed)
    }

    override fun solvePart2(): PuzzlePartSolution {
        val elapsed = measureTimeMillis {
            result = inputData.map { data -> this.calculateFuelWithAddOn(data.toLong()) }.sum()
        }
        return PuzzlePartSolution(2, result.toString(), elapsed)
    }

    fun calculateFuelWithAddOn(mass: Long): Long {
        var fuel = mass
        var totalFuel = 0L
        while (fuel > 0) {
            fuel = fuel / 3 - 2
            if (fuel > 0)
                totalFuel += fuel
        }
        return totalFuel
    }

}