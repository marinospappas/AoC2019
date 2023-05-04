package mpdev.springboot.aoc2019.solutions.day25

import mpdev.springboot.aoc2019.model.PuzzlePartSolution
import mpdev.springboot.aoc2019.solutions.PuzzleSolver
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import kotlin.system.measureTimeMillis

@Component
@Order(25)
class Day25TextBasedGame(@Autowired var inputProcessor: InputProcessor25): PuzzleSolver() {

    final override fun setDay() {
        day = 25         ////// update this when a puzzle solver for a new day is implemented
    }

    init {
        setDay()
    }

    private lateinit var program: Program
    var result = ""

    override fun initSolver() {
        program = inputProcessor.process(inputData)
    }

    override fun solvePart1(): PuzzlePartSolution {
        val elapsed = measureTimeMillis {
            program.run()
            if (InputOutput.output.isNotEmpty())
                result = InputOutput.output.last().toString()
        }
        // items:
        //    antenna
        //    semiconductor
        //    hypercube
        //    mouse
        return PuzzlePartSolution(1, result, elapsed)
    }

    override fun solvePart2(): PuzzlePartSolution {
        return PuzzlePartSolution(2, "", 0)
    }

}