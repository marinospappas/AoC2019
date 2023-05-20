package mpdev.springboot.aoc2019.solutions.day15

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import mpdev.springboot.aoc2019.model.PuzzlePartSolution
import mpdev.springboot.aoc2019.solutions.PuzzleSolver
import mpdev.springboot.aoc2019.solutions.icvm.ICVM
import org.springframework.stereotype.Component
import kotlin.system.measureTimeMillis

@Component
class Day15: PuzzleSolver() {

    final override fun setDay() {
        day = 15         ////// update this when a puzzle solver for a new day is implemented
    }

    init {
        setDay()
    }

    var result = 0

    private lateinit var icvm: ICVM
    private lateinit var droid: RepairDroid

    override fun initSolver() {
        icvm = ICVM(inputData[0])
        droid = RepairDroid(icvm)
    }

    override fun solvePart1(): PuzzlePartSolution {
        log.info("solving day $day part 1")
        var elapsed: Long
        runBlocking {
            elapsed = measureTimeMillis {
                //TODO: very slow implementation needs improvement
                val job = launch { icvm.runProgram() }
                droid.explore()
                job.cancel()
                icvm.waitProgram(job)
                result = droid.path(0 to 0, droid.oxygen, droid.spaces).size
            }
        }
        return PuzzlePartSolution(1, result.toString(), elapsed)
    }

    override fun solvePart2(): PuzzlePartSolution {
        log.info("solving day $day part 2")
        // needs part 1 completed first
        val elapsed = measureTimeMillis {
            val oxygen = droid.oxygen
            result = droid.spaces.maxOfOrNull { droid.path(oxygen, it, droid.spaces).size }!!
        }
        return PuzzlePartSolution(2, result.toString(), elapsed)
    }

}