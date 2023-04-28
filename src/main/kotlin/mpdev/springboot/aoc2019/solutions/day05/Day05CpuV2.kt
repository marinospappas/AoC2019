package mpdev.springboot.aoc2019.solutions.day05

import mpdev.springboot.aoc2019.model.PuzzlePartSolution
import mpdev.springboot.aoc2019.solutions.PuzzleSolver
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import kotlin.system.measureTimeMillis

@Component
@Order(5)
class Day05CpuV2(@Autowired var inputProcessor: InputProcessor05): PuzzleSolver() {

    var output = 19690720

    override fun setDay() {
        day = 5         ////// update this when a puzzle solver for a new day is implemented
    }

    private lateinit var program: Program
    var result = 0

    override fun initSolver() {
        program = inputProcessor.process(inputData)
    }

    override fun solvePart1(): PuzzlePartSolution {
        val elapsed = measureTimeMillis {
            InputOutput.input = 1
            program.restoreMem()
            program.run()
            result = InputOutput.output.last()
        }
        return PuzzlePartSolution(1, result.toString(), elapsed)
    }

    override fun solvePart2(): PuzzlePartSolution {
        val elapsed = measureTimeMillis {
            InputOutput.input = 5
            program.restoreMem()
            program.run()
            result = InputOutput.output.last()
        }
        return PuzzlePartSolution(2, result.toString(), elapsed)
    }

}