package mpdev.springboot.aoc2019.solutions.day02

import mpdev.springboot.aoc2019.model.PuzzlePartSolution
import mpdev.springboot.aoc2019.solutions.PuzzleSolver
import mpdev.springboot.aoc2019.solutions.icvm.ICProgram
import org.springframework.stereotype.Component
import kotlin.system.measureTimeMillis

@Component
class Day02: PuzzleSolver() {

    var part2Output = 19690720L

    final override fun setDay() {
        day = 2         ////// update this when a puzzle solver for a new day is implemented
    }

    init {
        setDay()
    }

    var result = 0L

    override fun initSolver() {}

    override fun solvePart1(): PuzzlePartSolution {
        val program = ICProgram(inputData[0])
        val elapsed = measureTimeMillis {
            program.setMemory(1, 12)
            program.setMemory(2, 2)
            program.run()
            result = program.getMemory(0L)
        }
        return PuzzlePartSolution(1, result.toString(), elapsed)
    }

    override fun solvePart2(): PuzzlePartSolution {
        result = 0
        val elapsed = measureTimeMillis {
            mainloop@ for (mem1 in 0..99)
                for (mem2 in 0..99) {
                    val program = ICProgram(inputData[0])
                    program.setLimitedMemory()
                    program.setMemory(1, mem1)
                    program.setMemory(2, mem2)
                    program.run()
                    if (program.getMemory(0) == part2Output) {
                        result = 100L * mem1 + mem2
                        break@mainloop
                    }
                }
        }
        return PuzzlePartSolution(2, result.toString(), elapsed)
    }

}