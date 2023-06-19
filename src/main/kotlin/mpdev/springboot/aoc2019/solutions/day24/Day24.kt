package mpdev.springboot.aoc2019.solutions.day24

import mpdev.springboot.aoc2019.model.PuzzlePartSolution
import mpdev.springboot.aoc2019.solutions.PuzzleSolver
import org.springframework.stereotype.Component
import kotlin.system.measureTimeMillis

@Component
class Day24: PuzzleSolver() {

    final override fun setDay() {
        day = 24         ////// update this when a puzzle solver for a new day is implemented
    }

    init {
        setDay()
    }

    var NUM_OF_CYCLES = 200
    var map = BugTiles()
    var result = 0

    override fun initSolver() {
        map.initPanels2D(inputData)
    }

    override fun solvePart1(): PuzzlePartSolution {
        log.info("solving day $day part 1")
        map.initPanels2D(inputData)
        val elapsed = measureTimeMillis {
            map.findRepeatedState()
        }
        map.printGrid2D()
        result = map.getBioDiversity()
        return PuzzlePartSolution(1, result.toString(), elapsed)
    }

    override fun solvePart2(): PuzzlePartSolution {
        log.info("solving day $day part 2")
        map.initPanels3D(inputData)
        val elapsed = measureTimeMillis {
            repeat(NUM_OF_CYCLES) { map.run1cycle3D() }
        }
        map.printGrid3D()
        result = map.countBugs3D()
        return PuzzlePartSolution(2, result.toString(), elapsed)
    }

}