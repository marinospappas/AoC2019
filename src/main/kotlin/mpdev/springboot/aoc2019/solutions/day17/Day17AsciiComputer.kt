package mpdev.springboot.aoc2019.solutions.day17

import mpdev.springboot.aoc2019.model.PuzzlePartSolution
import mpdev.springboot.aoc2019.solutions.PuzzleSolver
import mpdev.springboot.aoc2019.utils.big
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import java.lang.StringBuilder
import kotlin.system.measureTimeMillis

@Component
@Order(17)
class Day17AsciiComputer(@Autowired var inputProcessor: InputProcessor17): PuzzleSolver() {

    override fun setDay() {
        day = 17         ////// update this when a puzzle solver for a new day is implemented
    }

    private lateinit var program: Program
    var result = 0

    override fun initSolver() {
        program = inputProcessor.process(inputData)
    }

    override fun solvePart1(): PuzzlePartSolution {
        val elapsed = measureTimeMillis {
            program.run()
            val output = StringBuilder().also { s -> InputOutput.output.forEach { n -> s.append(n.toInt().toChar()) } }
                .toString().trim('\n')
            val asciiProcessor = AsciiProcessor(output)
            result = asciiProcessor.process()
        }
        return PuzzlePartSolution(1, result.toString(), elapsed)
    }

    override fun solvePart2(): PuzzlePartSolution {
        var result: String
        val elapsed = measureTimeMillis {
            val inputData2 = mutableListOf<String>().also { it.add(inputData[0].replaceFirst("1", "2")) }
            program = inputProcessor.process(inputData2)
            InputOutput.output = mutableListOf()
            InputOutput.setInputValues()
            program.run()
            println(StringBuilder().also { s -> InputOutput.output.forEach { n -> s.append(n.toInt().toChar()) } }
                .toString().trim('\n'))
            result = InputOutput.output.last().toString()
        }
        return PuzzlePartSolution(2, result, elapsed)
    }

}