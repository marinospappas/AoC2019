package mpdev.springboot.aoc2019.solutions.day25

import mpdev.springboot.aoc2019.model.PuzzlePartSolution
import mpdev.springboot.aoc2019.solutions.PuzzleSolver
import mpdev.springboot.aoc2019.utils.big
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import java.lang.StringBuilder
import kotlin.system.measureTimeMillis

@Component
@Order(25)
class Day25TextBasedGame(@Autowired var inputProcessor: InputProcessor25): PuzzleSolver() {

    override fun setDay() {
        day = 25         ////// update this when a puzzle solver for a new day is implemented
    }

    private lateinit var program: Program
    var result = ""

    override fun initSolver() {
        program = inputProcessor.process(inputData)
    }

    override fun solvePart1(): PuzzlePartSolution {
        val elapsed = measureTimeMillis {
            program.run()
            println(StringBuilder().also { s -> InputOutput.output.forEach { n -> s.append(n.toInt().toChar()) } }
                .toString().trim('\n'))
            result = InputOutput.output.last().toString()
        }
        // items:
        //  antenna
        //  semiconductor
        //  hypercube
        //  mouse
        return PuzzlePartSolution(1, result, elapsed)
    }

    override fun solvePart2(): PuzzlePartSolution {
        var result = ""
        val elapsed = measureTimeMillis {
        }
        return PuzzlePartSolution(2, result, elapsed)
    }

}