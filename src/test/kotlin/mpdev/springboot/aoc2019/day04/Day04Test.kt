package mpdev.springboot.aoc2019.day04

import mpdev.springboot.aoc2019.input.InputDataReader
import mpdev.springboot.aoc2019.solutions.day04.Day04
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class Day04Test {

    private val day = 4                                     ///////// Update this for a new dayN test
    private val puzzleSolver = Day04()                        ///////// Update this for a new dayN test
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
        "123446, true",
        "123456, false",
        "12345, false",
        "123454, false",
        "111111, true"
    )
    @Order(2)
    fun `Assesses Password Rules Part 1`(pwd: Int, expected: Boolean) {
        val result = puzzleSolver.satisfiesPwdRules(pwd){ len -> len >= 2 }
        assertThat(result).isEqualTo(expected)
    }

    @ParameterizedTest
    @CsvSource(
        "123446, true",
        "123456, false",
        "12345, false",
        "123454, false",
        "111111, false",
        "111223, true",
        "111133, true",
        "111333, false",
    )
    @Order(3)
    fun `Assesses Password Rules Part 2`(pwd: Int, expected: Boolean) {
        val result = puzzleSolver.satisfiesPwdRules(pwd){ len -> len == 2 }
        assertThat(result).isEqualTo(expected)
    }
}
