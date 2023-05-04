package mpdev.springboot.aoc2019.day01

import mpdev.springboot.aoc2019.input.InputDataReader
import mpdev.springboot.aoc2019.solutions.day1.Day01
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class Day01Test {

    private val day = 1                                     ///////// Update this for a new dayN test
    private val puzzleSolver = Day01()                        ///////// Update this for a new dayN test
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

    @ParameterizedTest
    @CsvSource(
        "14, 2",
        "1969, 966",
        "100756, 50346"
    )
    @Order(2)
    fun `Solves Part 2`(mass: Long, expected: Long) {
        val result = puzzleSolver.calculateFuelWithAddOn(mass)
        assertThat(result).isEqualTo(expected)
    }
}
