package mpdev.springboot.aoc2019.day16

import mpdev.springboot.aoc2019.input.InputDataReader
import mpdev.springboot.aoc2019.solutions.day16.Day16
import mpdev.springboot.aoc2019.solutions.day16.Fft
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class Day16Test {

    private val day = 16                                     ///////// Update this for a new dayN test
    private val puzzleSolver = Day16()                        ///////// Update this for a new dayN test
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
    fun `Reads and Processes Input Correctly`() {
        val fft = Fft(inputLines[0])
        fft.signal.forEach { print("$it, ") }
        println()
        assertThat(fft.signal.size).isEqualTo(8)
    }

    @Test
    @Order(3)
    fun `Calculates Pattern for Index`() {
        val fft = Fft(inputLines[0])
        assertThat(fft.calculatePatternForIndex(0).toList()).isEqualTo(listOf(1,0,-1,0,1,0,-1,0))
        assertThat(fft.calculatePatternForIndex(1).toList()).isEqualTo(listOf(0,1,1,0,0,-1,-1,0))
        assertThat(fft.calculatePatternForIndex(2).toList()).isEqualTo(listOf(0,0,1,1,1,0,0,0))
        assertThat(fft.calculatePatternForIndex(3).toList()).isEqualTo(listOf(0,0,0,1,1,1,1,0))
    }

    @ParameterizedTest
    @CsvSource(
        "1, 48226158",
        "2, 34040438",
        "3, 03415518",
        "4, 01029498",
    )
    @Order(4)
    fun `Transforms Signal`(numberOfPhases: Int, expected: String) {
        val fft = Fft(inputLines[0])
        repeat(numberOfPhases) { fft.transform() }
        assertThat(fft.signal.toList().joinToString("")).isEqualTo(expected)
    }

    @ParameterizedTest
    @CsvSource(
        "80871224585914546619083218645595, 24176176",
        "19617804207202209144916044189917, 73745418",
        "69317163492948606335995924319873, 52432133",
    )
    @Order(5)
    fun `Solves Part 1`(input: String, expected: Long) {
        puzzleSolver.inputData = listOf(input)
        puzzleSolver.initSolver()
        puzzleSolver.solvePart1()
        assertThat(puzzleSolver.result).isEqualTo(expected)
    }

    @ParameterizedTest
    @CsvSource(
    )
    @Order(6)
    fun `Solves Part 2`(index: Int, expected: Long) {
        puzzleSolver.initSolver()
        puzzleSolver.solvePart2()
        assertThat(puzzleSolver.result).isEqualTo(expected)
    }
}
