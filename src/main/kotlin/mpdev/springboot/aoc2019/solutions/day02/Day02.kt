package mpdev.springboot.aoc2019.solutions.day02

import mpdev.springboot.aoc2019.model.PuzzlePartSolution
import mpdev.springboot.aoc2019.solutions.PuzzleSolver
import mpdev.springboot.aoc2019.solutions.icvm.ICVM
import org.springframework.stereotype.Component
import kotlin.system.measureTimeMillis

@Component
class Day02: PuzzleSolver() {

    var part2Output = 19690720

    final override fun setDay() {
        day = 2         ////// update this when a puzzle solver for a new day is implemented
    }

    init {
        setDay()
    }

    var result = 0

    override fun initSolver() {}

    override fun solvePart1(): PuzzlePartSolution {
        log.info("solving day $day part 2")
        val icvm = ICVM(inputData[0])
        val elapsed = measureTimeMillis {
            icvm.setProgramMemory(1, 12)
            icvm.setProgramMemory(2, 2)
            icvm.runProgram()
            icvm.waitProgram()
            result = icvm.getProgramMemory(0)
        }
        return PuzzlePartSolution(1, result.toString(), elapsed)
    }

    override fun solvePart2(): PuzzlePartSolution {
        log.info("solving day $day part 2")
        result = 0
        val elapsed = measureTimeMillis {
            mainloop@ for (mem1 in 0..99)
                for (mem2 in 0..99) {
                    val icvm = ICVM(inputData[0])
                    icvm.setLimitedMemory()
                    icvm.setProgramMemory(1, mem1)
                    icvm.setProgramMemory(2, mem2)
                    icvm.runProgram()
                    icvm.waitProgram()
                    if (icvm.getProgramMemory(0) == part2Output) {
                        result = 100 * mem1 + mem2
                        break@mainloop
                    }
                }
        }
        return PuzzlePartSolution(2, result.toString(), elapsed)
    }

}