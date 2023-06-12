package mpdev.springboot.aoc2019.day06

import mpdev.springboot.aoc2019.input.InputDataReader
import mpdev.springboot.aoc2019.solutions.day06.Day06
import mpdev.springboot.aoc2019.solutions.day06.ObjectInOrbit
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class Day06Test {

    private val day = 6                                     ///////// Update this for a new dayN test
    private val puzzleSolver = Day06()                        ///////// Update this for a new dayN test
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
    @Order(2)
    fun `Reads And Processes Input Correctly`() {
        puzzleSolver.orbitMap.map.forEach { (k, v) -> println("$k ( $v") }
        assertThat(puzzleSolver.orbitMap.map.size).isEqualTo(11)
    }

    @ParameterizedTest
    @CsvSource(
        "D, 3", "L, 7", "COM, 0"
    )
    @Order(3)
    fun `Counts Orbits Correctly`(name: String, expected: Int) {
        puzzleSolver.initSolver()
        assertThat(puzzleSolver.orbitMap.countOrbits(name)).isEqualTo(expected)
    }

    @Test
    @Order(4)
    fun `Solves Part 1`() {
        puzzleSolver.initSolver()
        puzzleSolver.solvePart1()
        assertThat(puzzleSolver.result).isEqualTo(42)
    }

    @ParameterizedTest
    @Order(5)
    @CsvSource(
        "'R8,U5,L5,D3', 'U7,R6,D4,L4', 30",
    )
    fun `Solves Part 2`(input1: String, input2: String, expected: Int) {
        puzzleSolver.inputData = listOf(input1, input2)
        puzzleSolver.initSolver()
        puzzleSolver.solvePart2()
        assertThat(puzzleSolver.result).isEqualTo(expected)
    }
}
