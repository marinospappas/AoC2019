package mpdev.springboot.aoc2019.solutions.day07

import mpdev.springboot.aoc2019.model.PuzzlePartSolution
import mpdev.springboot.aoc2019.solutions.PuzzleSolver
import mpdev.springboot.aoc2019.utils.AocUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.math.BigInteger
import kotlin.concurrent.thread
import kotlin.system.measureTimeMillis

@Component
class Day07CpuV3(@Autowired var inputProcessor: InputProcessor07): PuzzleSolver() {

    companion object {
        const val NUMBER_OF_AMPS = 5
        private val log: Logger = LoggerFactory.getLogger(this::class.java)
    }

    final override fun setDay() {
        day = 7         ////// update this when a puzzle solver for a new day is implemented
    }

    init {
        setDay()
    }

    var result: BigInteger = BigInteger.valueOf(0)

    override fun initSolver() {}

    override fun solvePart1(): PuzzlePartSolution {
        val thrusts = mutableListOf<BigInteger>()
        val elapsed = measureTimeMillis {
            AocUtils.permutations(mutableListOf(0,1,2,3,4)).forEach { phaseSequence ->
                thrusts.add(calculateTotalThrust(phaseSequence))
            }
            result = thrusts.max()
        }
        return PuzzlePartSolution(1, result.toString(), elapsed)
    }

    override fun solvePart2(): PuzzlePartSolution {
        val thrusts = mutableListOf<BigInteger>()
        val elapsed = measureTimeMillis {
            AocUtils.permutations(mutableListOf(5,6,7,8,9)).forEach { phaseSequence ->
                thrusts.add(calculateTotalThrust(phaseSequence, true))
            }
            result = thrusts.max()
        }
        return PuzzlePartSolution(2, result.toString(), elapsed)
    }

    fun calculateTotalThrust(phaseSequence: List<Int>, loop: Boolean = false): BigInteger {
        InputOutput.initInputOutput(NUMBER_OF_AMPS, loop)
        log.info("processing sequence $phaseSequence")
        // prepare the inputs
        phaseSequence.indices.forEach {
            InputOutput.setInputValues(it,
                if (it == 0)
                    listOf(BigInteger.valueOf(phaseSequence[it].toLong()), BigInteger.valueOf(0L))
                else
                    listOf(BigInteger.valueOf(phaseSequence[it].toLong())))
        }
        // execute the 5 copies of the program in 5 threads
        val amplifiers = Array(NUMBER_OF_AMPS) {
            thread(start = true, name = "amplifier-$it") {
                val program = inputProcessor.process(inputData)
                Thread.sleep(2)
                program.run()
            }
        }
        amplifiers.forEach { t -> t.join() }
        val result = InputOutput.getOutputValues(NUMBER_OF_AMPS-1).last()
        log.info("result: $result")
        return result
    }

}