package mpdev.springboot.aoc2019.solutions.day07

import mpdev.springboot.aoc2019.model.PuzzlePartSolution
import mpdev.springboot.aoc2019.solutions.PuzzleSolver
import mpdev.springboot.aoc2019.utils.AocUtils
import mpdev.springboot.aoc2019.solutions.icvm.InputOutput.initIoChannels
import mpdev.springboot.aoc2019.solutions.icvm.InputOutput.setInputValues
import mpdev.springboot.aoc2019.solutions.icvm.InputOutput.getOutputValues
import mpdev.springboot.aoc2019.solutions.icvm.ICProgram
import org.springframework.stereotype.Component
import kotlin.concurrent.thread
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

    var result = 0L

    override fun initSolver() {}

    override fun solvePart1(): PuzzlePartSolution {
        val thrusts = mutableListOf<Long>()
        val elapsed = measureTimeMillis {
            AocUtils.permutations(mutableListOf(0,1,2,3,4)).forEach { phaseSequence ->
                thrusts.add(calculateTotalThrust(phaseSequence))
            }
            result = thrusts.max()
        }
        return PuzzlePartSolution(1, result.toString(), elapsed)
    }

    override fun solvePart2(): PuzzlePartSolution {
        val thrusts = mutableListOf<Long>()
        val elapsed = measureTimeMillis {
            AocUtils.permutations(mutableListOf(5,6,7,8,9)).forEach { phaseSequence ->
                thrusts.add(calculateTotalThrust(phaseSequence, true))
            }
            result = thrusts.max()
        }
        return PuzzlePartSolution(2, result.toString(), elapsed)
    }

    fun calculateTotalThrust(phaseSequence: List<Int>, loop: Boolean = false): Long {
        initIoChannels(NUMBER_OF_AMPS, loop)
        log.debug("processing sequence {}", phaseSequence)
        // prepare the inputs
        phaseSequence.indices.forEach {
            setInputValues(
                if (it == 0)
                    listOf(phaseSequence[it].toLong(), 0L)
                else
                    listOf(phaseSequence[it].toLong()),
                it)
        }
        // execute the 5 copies of the intcode program in 5 threads
        val amplifiers = Array(NUMBER_OF_AMPS) {
            //TODO: there is some race condition here - needs more debugging
            thread(start = true, name = "amplifier-$it") {
                val program = ICProgram(inputData[0])
                //Thread.sleep(5)
                program.run()
            }
        }
        amplifiers.forEach { t -> t.join() }
        val result = getOutputValues(NUMBER_OF_AMPS-1).last()
        log.debug("result: {}", result)
        return result
    }

}