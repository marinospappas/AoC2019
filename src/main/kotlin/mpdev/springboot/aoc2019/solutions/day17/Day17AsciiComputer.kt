package mpdev.springboot.aoc2019.solutions.day17

import mpdev.springboot.aoc2019.model.PuzzlePartSolution
import mpdev.springboot.aoc2019.solutions.PuzzleSolver
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import java.lang.StringBuilder
import kotlin.system.measureTimeMillis

@Component
@Order(17)
class Day17AsciiComputer(@Autowired var inputProcessor: InputProcessor17): PuzzleSolver() {

    companion object {
        private val log: Logger = LoggerFactory.getLogger(this::class.java)
    }

    final override fun setDay() {
        day = 17         ////// update this when a puzzle solver for a new day is implemented
    }

    init {
        setDay()
    }

    private lateinit var program: Program
    var result = 0

    override fun initSolver() {
        program = inputProcessor.process(inputData)
    }

    override fun solvePart1(): PuzzlePartSolution {
        log.info("solving day 17 part 1")
        InputOutput.initInputOutput()
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
        log.info("solving day 17 part 2")
        var result: String
        InputOutput.initInputOutput()
        val elapsed = measureTimeMillis {
            val inputData2 = mutableListOf<String>().also { it.add(inputData[0].replaceFirst("1", "2")) }
            program = inputProcessor.process(inputData2)
            InputOutput.output = mutableListOf()
            program.run()
            result = InputOutput.output.last().toString()
        }
        println(StringBuilder().also { s -> InputOutput.output.forEach { n -> s.append(n.toInt().toChar()) } }
            .toString().trim('\n'))
        return PuzzlePartSolution(2, result, elapsed)
    }

}