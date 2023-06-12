package mpdev.springboot.aoc2019.day08

import mpdev.springboot.aoc2019.input.InputDataReader
import mpdev.springboot.aoc2019.solutions.day08.Day08
import mpdev.springboot.aoc2019.solutions.day08.Image
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test

class Day08Test {

    private val day = 8                                     ///////// Update this for a new dayN test
    private val puzzleSolver = Day08()                        ///////// Update this for a new dayN test
    private val inputDataReader = InputDataReader("src/test/resources/inputdata/input")
    private var inputLines: MutableList<String> = inputDataReader.read(day).toMutableList()

    @BeforeEach
    fun setup() {
        puzzleSolver.setDay()
        puzzleSolver.inputData = inputLines
        Image.WIDTH = 2
        Image.HEIGHT = 2
        puzzleSolver.initSolver()
    }

    @Test
    @Order(1)
    fun `Sets Day correctly`() {
        assertThat(puzzleSolver.day).isEqualTo(day)
    }

    @Test
    @Order(2)
    fun `Reads And Processes Input Correctly`() {
        assertThat(puzzleSolver.inputData.size / Image.HEIGHT / Image.WIDTH).isEqualTo(0)
        assertThat(puzzleSolver.image.layers.size).isEqualTo(4)
    }

    @Test
    @Order(4)
    fun `Solves Part 1`() {
        puzzleSolver.solvePart1()
        assertThat(puzzleSolver.result).isEqualTo(4)
    }

    @Test
    @Order(5)
    fun `Solves Part 2`() {
        puzzleSolver.solvePart2()
    }
}
