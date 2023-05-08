package mpdev.springboot.aoc2019.day17

import mpdev.springboot.aoc2019.input.InputDataReader
import mpdev.springboot.aoc2019.solutions.day17.AsciiProcessor
import mpdev.springboot.aoc2019.solutions.day17.Day17
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test

class Day17Test {

    private val day = 17                       ///////// Update this for a new dayN test
    private val puzzleSolver = Day17()         ///////// Update this for a new dayN test
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
