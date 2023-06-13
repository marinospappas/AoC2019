package mpdev.springboot.aoc2019.solutions.day12

import mpdev.springboot.aoc2019.model.PuzzlePartSolution
import mpdev.springboot.aoc2019.solutions.PuzzleSolver
import mpdev.springboot.aoc2019.utils.AocException
import org.springframework.stereotype.Component
import kotlin.system.measureTimeMillis

@Component
class Day12: PuzzleSolver() {

    final override fun setDay() {
        day = 12         ////// update this when a puzzle solver for a new day is implemented
    }

    init {
        setDay()
    }

    var STEPS = 1000
    lateinit var moons: MutableList<Moon>
    var result = 0

    override fun initSolver() {
        moons = mutableListOf()
        inputData.forEach { inputLine ->
            val match = Regex("""([A-Z][a-z]+) <x=(-?\d+), y=(-?\d+), z=(-?\d+)>""").find(inputLine)
            try {
                val (name, x, y, z) = match!!.destructured
                moons.add(Moon(name, Point3D(x.toInt(), y.toInt(), z.toInt())))
            } catch (e: Exception) {
                throw AocException("bad input line $inputLine")
            }
        }
    }

    override fun solvePart1(): PuzzlePartSolution {
        result = 0
        val elapsed = measureTimeMillis {
            repeat(STEPS) {
                applyGravity()
                moons.forEach { moon -> moon.applyVelocity() }
            }
            result = moons.sumOf { it.calculateEnergy() }
        }
        return PuzzlePartSolution(1, result.toString(), elapsed)
    }

    override fun solvePart2(): PuzzlePartSolution {
        result = 0
        val elapsed = measureTimeMillis {
        }
        return PuzzlePartSolution(2, result.toString(), elapsed)
    }

    fun applyGravity() {
        val newVelocities = mutableListOf<Point3D>()
        moons.forEach { moon -> newVelocities.add(moon.applyGravity(moons)) }
        moons.indices.forEach { moons[it].velocity = newVelocities[it] }
    }
}