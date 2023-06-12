package mpdev.springboot.aoc2019.day03

import mpdev.springboot.aoc2019.input.InputDataReader
import mpdev.springboot.aoc2019.solutions.day03.Day03
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class Day03Test {

    private val day = 3                                     ///////// Update this for a new dayN test
    private val puzzleSolver = Day03()                        ///////// Update this for a new dayN test
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
        println(puzzleSolver.wire1.path)
        println(puzzleSolver.wire1.pointsList)
        println(puzzleSolver.wire2.path)
        println(puzzleSolver.wire2.pointsList)
        assertThat(puzzleSolver.wire1.pointsList.size).isEqualTo(22)
        assertThat(puzzleSolver.wire2.pointsList.size).isEqualTo(22)
    }

    @ParameterizedTest
    @Order(3)
    @CsvSource(
        "'R8,U5,L5,D3', 'U7,R6,D4,L4', 6",
        "'R75,D30,R83,U83,L12,D49,R71,U7,L72', 'U62,R66,U55,R34,D71,R55,D58,R83', 159",
        "'R98,U47,R26,D63,R33,U87,L62,D20,R33,U53,R51', 'U98,R91,D20,R16,D67,R40,U7,R15,U6,R7', 135"
    )
    fun `Solves Part 1`(input1: String, input2: String, expected: Int) {
        puzzleSolver.inputData = listOf(input1, input2)
        puzzleSolver.initSolver()
        puzzleSolver.solvePart1()
        assertThat(puzzleSolver.result).isEqualTo(expected)
    }

    @ParameterizedTest
    @Order(4)
    @CsvSource(
        "'R8,U5,L5,D3', 'U7,R6,D4,L4', 30",
        "'R75,D30,R83,U83,L12,D49,R71,U7,L72', 'U62,R66,U55,R34,D71,R55,D58,R83', 610",
        "'R98,U47,R26,D63,R33,U87,L62,D20,R33,U53,R51', 'U98,R91,D20,R16,D67,R40,U7,R15,U6,R7', 410"
    )
    fun `Solves Part 2`(input1: String, input2: String, expected: Int) {
        puzzleSolver.inputData = listOf(input1, input2)
        puzzleSolver.initSolver()
        puzzleSolver.solvePart2()
        assertThat(puzzleSolver.result).isEqualTo(expected)
    }
}
