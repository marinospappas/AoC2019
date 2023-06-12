package mpdev.springboot.aoc2019.solutions.day03

import mpdev.springboot.aoc2019.model.PuzzlePartSolution
import mpdev.springboot.aoc2019.solutions.PuzzleSolver
import mpdev.springboot.aoc2019.utils.manhattan
import org.springframework.stereotype.Component
import java.awt.Point
import kotlin.system.measureTimeMillis

@Component
class Day03: PuzzleSolver() {

    final override fun setDay() {
        day = 3         ////// update this when a puzzle solver for a new day is implemented
    }

    init {
        setDay()
    }

    lateinit var wire1: Wire
    lateinit var wire2: Wire

    var result = 0

    override fun initSolver() {
        wire1 = Wire(inputData[0])
        wire2 = Wire(inputData[1])
    }

    override fun solvePart1(): PuzzlePartSolution {
        result = 0
        val elapsed = measureTimeMillis {
            val intersections = wire1.pointsList intersect wire2.pointsList - Point(0,0)
            result = intersections.minOf { it.manhattan(Point(0,0)) }
        }
        return PuzzlePartSolution(1, result.toString(), elapsed)
    }

    override fun solvePart2(): PuzzlePartSolution {
        result = 0
        val elapsed = measureTimeMillis {
            val dist1 = wire1.calculateDistanceToPoints()
            val dist2 = wire2.calculateDistanceToPoints()
            val intersections = dist1.keys intersect dist2.keys - Point(0,0)
            val distances = mutableListOf<Int>()
            intersections.forEach { distances.add(dist1[it]!! + dist2[it]!!) }
            result = distances.min()
        }
        return PuzzlePartSolution(2, result.toString(), elapsed)
    }

}