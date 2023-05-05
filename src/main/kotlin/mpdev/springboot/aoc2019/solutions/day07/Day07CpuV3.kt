package mpdev.springboot.aoc2019.solutions.day07

import mpdev.springboot.aoc2019.model.PuzzlePartSolution
import mpdev.springboot.aoc2019.solutions.PuzzleSolver
import mpdev.springboot.aoc2019.utils.AocUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.math.BigInteger
import kotlin.system.measureTimeMillis

@Component
class Day07CpuV3(@Autowired var inputProcessor: InputProcessor07): PuzzleSolver() {

    final override fun setDay() {
        day = 7         ////// update this when a puzzle solver for a new day is implemented
    }

    init {
        setDay()
    }

    private lateinit var program: Program
    var result = BigInteger.valueOf(0)

    override fun initSolver() {
        program = inputProcessor.process(inputData)
        InputOutput.initInputOutput()
    }

    override fun solvePart1(): PuzzlePartSolution {
        val thrusts = mutableListOf<BigInteger>()
        val elapsed = measureTimeMillis {
            AocUtils.permutations(mutableListOf(0,1,2,3,4)).forEach { phaseSequence ->
                thrusts.add(calculateTotalThrustPart1(phaseSequence))
            }
            result = thrusts.max()
        }
        return PuzzlePartSolution(1, result.toString(), elapsed)
    }

    override fun solvePart2(): PuzzlePartSolution {
        val elapsed = measureTimeMillis {

        }
        return PuzzlePartSolution(2, result.toString(), elapsed)
    }

    fun calculateTotalThrustPart1(phaseSequence: List<Int>): BigInteger {
        var thrust = BigInteger.valueOf(0L)
        phaseSequence.forEach {
            thrust = runBoostProgram(BigInteger.valueOf(it.toLong()), thrust)
        }
        return thrust
    }

    fun runBoostProgram(input1: BigInteger, input2: BigInteger): BigInteger {
        initSolver()
        InputOutput.input.add(input1)
        InputOutput.input.add(input2)
        program.run()
        return InputOutput.output.last()
    }
}