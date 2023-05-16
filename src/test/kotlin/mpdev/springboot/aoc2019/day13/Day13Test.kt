package mpdev.springboot.aoc2019.day13

import mpdev.springboot.aoc2019.input.InputDataReader
import mpdev.springboot.aoc2019.solutions.day13.Day13
import mpdev.springboot.aoc2019.solutions.day17.AsciiProcessor
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test

class Day13Test {

    private val day = 13                       ///////// Update this for a new dayN test
    private val puzzleSolver = Day13()         ///////// Update this for a new dayN test
    private val inputDataReader = InputDataReader("src/test/resources/inputdata/input")
    private var inputLines: List<String> = inputDataReader.read(day)

    @BeforeEach
    fun setup() {
        puzzleSolver.setDay()
        puzzleSolver.inputData = inputLines
        puzzleSolver.initSolver()
    }

    @Test
    @Order(1)
    fun `Sets Day correctly`() {
        assertThat(puzzleSolver.day).isEqualTo(day)
    }


    @Test
    @Order(5)
    fun `Solves Part 1`() {
        val asciiProcessor = AsciiProcessor("""
            ..#..........
            ..#..........
            #######...###
            #.#...#...#.#
            #############
            ..#...#...#..
            ..#####...^..
        """.trimIndent())
        val result = asciiProcessor.process()
        assertThat(result).isEqualTo(76)
    }

}
