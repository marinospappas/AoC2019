package mpdev.springboot.aoc2019.solutions.day02

import mpdev.springboot.aoc2019.model.PuzzlePartSolution
import mpdev.springboot.aoc2019.solutions.PuzzleSolver
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import kotlin.system.measureTimeMillis

@Component
@Order(2)
class Day02CpuV1(@Autowired var inputProcessor: InputProcessor02): PuzzleSolver() {

    var output = 19690720

    final override fun setDay() {
        day = 2         ////// update this when a puzzle solver for a new day is implemented
    }

    init {
        setDay()
    }

    private lateinit var intCode: IntCode
    var result = 0

    override fun initSolver() {
        intCode = inputProcessor.process(inputData)
    }

    override fun solvePart1(): PuzzlePartSolution {
        val elapsed = measureTimeMillis {
            intCode.mem[1] = 12
            intCode.mem[2] = 2
            intCode.run()
            result = intCode.mem[0]
        }
        return PuzzlePartSolution(1, result.toString(), elapsed)
    }

    override fun solvePart2(): PuzzlePartSolution {
        result = 0
        val elapsed = measureTimeMillis {
            mainloop@ for (mem1 in 0..99)
                for (mem2 in 0..99) {
                    intCode.restoreMem()
                    intCode.mem[1] = mem1
                    intCode.mem[2] = mem2
                    intCode.run()
                    if (intCode.mem[0] == output) {
                        result = 100 * mem1 + mem2
                        break@mainloop
                    }
                }
        }
        return PuzzlePartSolution(2, result.toString(), elapsed)
    }



}