package mpdev.springboot.aoc2019.solutions.day12

import mpdev.springboot.aoc2019.model.PuzzlePartSolution
import mpdev.springboot.aoc2019.solutions.PuzzleSolver
import mpdev.springboot.aoc2019.utils.AocException
import org.springframework.stereotype.Component
import org.apache.commons.math3.util.ArithmeticUtils.lcm
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
    var result = 0L

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
        val elapsed = measureTimeMillis {
            repeat(STEPS) { applyMovement() }
            result = moons.sumOf { it.calculateEnergy() }.toLong()
        }
        return PuzzlePartSolution(1, result.toString(), elapsed)
    }

    override fun solvePart2(): PuzzlePartSolution {
        moons.forEach { moon -> moon.init() }
        val elapsed = measureTimeMillis {
            result = findCycle()
        }
        return PuzzlePartSolution(2, result.toString(), elapsed)
    }

    fun applyGravity() {
        val newVelocities = mutableListOf<Point3D>()
        moons.forEach { moon -> newVelocities.add(moon.applyGravity(moons)) }
        moons.indices.forEach { moons[it].velocity = newVelocities[it] }
    }

    fun applyMovement() {
        applyGravity()
        moons.forEach { moon -> moon.applyVelocity() }
    }

    fun findCycle(): Long {
        val cycles = IntArray(3) {0}        // calculate the cycle for x, y, z independent of each other
        var t = 0
        do {
            t += 1
            applyMovement()
            cycles.indices.forEach { i ->       // i is 0 for x, 1 for y, 2 for z
                if (cycles[i] == 0 && moons.all { moon -> moon.xyzMatchesInitialState(i) })
                   cycles[i] = t
            }
        } while (cycles.any { it == 0 })
        return lcm(cycles[2].toLong(), lcm(cycles[1].toLong(), cycles[0].toLong()))
    }
}