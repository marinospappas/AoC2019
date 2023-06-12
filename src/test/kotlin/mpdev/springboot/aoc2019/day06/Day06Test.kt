package mpdev.springboot.aoc2019.day06

import mpdev.springboot.aoc2019.input.InputDataReader
import mpdev.springboot.aoc2019.solutions.day06.Day06
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
    private var inputLines: MutableList<String> = inputDataReader.read(day).toMutableList()

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
    @Order(3)
    @CsvSource(
        "D, 3", "L, 7", "COM, 0"
    )
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

    @Test
    @Order(5)
    fun `Solves Part 2`() {
        inputLines.add("K)YOU")
        inputLines.add("I)SAN")
        puzzleSolver.inputData = inputLines
        puzzleSolver.initSolver()
        puzzleSolver.solvePart2()
        assertThat(puzzleSolver.result).isEqualTo(4)
    }
}
