package mpdev.springboot.aoc2019.solutions.day07

import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import mpdev.springboot.aoc2019.model.PuzzlePartSolution
import mpdev.springboot.aoc2019.solutions.PuzzleSolver
import mpdev.springboot.aoc2019.utils.AocUtils
import mpdev.springboot.aoc2019.solutions.icvm.ICVMMultipleInstances
import mpdev.springboot.aoc2019.solutions.icvm.ICVMMultipleInstancesc
import mpdev.springboot.aoc2019.solutions.icvm.IOMode
import org.springframework.stereotype.Component
import kotlin.system.measureTimeMillis

@Component
class Day07: PuzzleSolver() {

    companion object {
        const val NUMBER_OF_AMPS = 5
    }

    final override fun setDay() {
        day = 7         ////// update this when a puzzle solver for a new day is implemented
    }

    init {
        setDay()
    }

    var result = 0

    override fun initSolver() {}

    override fun solvePart1(): PuzzlePartSolution {
        log.info("solving day $day part 1")
        val thrusts = mutableListOf<Int>()
        val elapsed = measureTimeMillis {
            AocUtils.permutations(mutableListOf(0,1,2,3,4)).forEach { phaseSequence ->
                thrusts.add(calculateTotalThrust(phaseSequence))
            }
            result = thrusts.max()
        }
        return PuzzlePartSolution(1, result.toString(), elapsed)
    }

    override fun solvePart2(): PuzzlePartSolution {
        log.info("solving day $day part 2")
        val thrusts = mutableListOf<Int>()
        val elapsed = measureTimeMillis {
            AocUtils.permutations(mutableListOf(5,6,7,8,9)).forEach { phaseSequence ->
                thrusts.add(calculateTotalThrust(phaseSequence, true))
            }
            result = thrusts.max()
        }
        return PuzzlePartSolution(2, result.toString(), elapsed)
    }

    fun calculateTotalThrust(phaseSequence: List<Int>, loop: Boolean = false): Int {
        log.debug("processing sequence {}", phaseSequence)
        var result: Int
        // setup the 5 instances of the IntCode program
        val icvm = ICVMMultipleInstancesc(inputData[0])
        repeat(NUMBER_OF_AMPS - 2) { _ -> icvm.cloneInstance(IOMode.PIPE)}
        icvm.cloneInstance(IOMode.PIPE, loop)
        runBlocking {
            // prepare the inputs
            phaseSequence.indices.forEach {
                if (it == 0)
                    icvm.setInstanceInput(listOf(phaseSequence[it], 0), 0)
                else
                    icvm.setInstanceInput(phaseSequence[it], it)
            }
            // execute the 5 copies of the intCode program in 5 coroutines
            val jobs = Array(NUMBER_OF_AMPS) {
                launch { icvm.runInstance(it) }
            }
            // and wait until all complete
            repeat(NUMBER_OF_AMPS) { icvm.waitInstance(it, jobs[it]) }
            result = icvm.getInstanceOutput(NUMBER_OF_AMPS - 1).last()
        }
        log.debug("result: {}", result)
        return result
    }

}