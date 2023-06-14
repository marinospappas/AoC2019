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
        //index 0: (1,0,-1,0,1,0,-1,0)
        assertThat(fft.calculatePatternForIndex(0).first).isEqualTo(setOf(0,4))
        assertThat(fft.calculatePatternForIndex(0).second).isEqualTo(setOf(2,6))
        //index 1: (0,1,1,0,0,-1,-1,0)
        assertThat(fft.calculatePatternForIndex(1).first).isEqualTo(setOf(1,2))
        assertThat(fft.calculatePatternForIndex(1).second).isEqualTo(setOf(5,6))
        //index 2: (0,0,1,1,1,0,0,0)
        assertThat(fft.calculatePatternForIndex(2).first).isEqualTo(setOf(2,3,4))
        assertThat(fft.calculatePatternForIndex(2).second).isEqualTo(setOf<Int>())
        //index 3: (0,0,0,1,1,1,1,0)
        assertThat(fft.calculatePatternForIndex(3).first).isEqualTo(setOf(3,4,5,6))
        assertThat(fft.calculatePatternForIndex(3).second).isEqualTo(setOf<Int>())
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
        fft.initPatterns()
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
        "03036732577212944063491565474664, 84462026",
        "02935109699940807407585447034323, 78725270",
        "03081770884921959731165446850517, 53553731",
    )
    @Order(6)
    fun `Solves Part 2`(input: String, expected: Long) {
        puzzleSolver.inputData = listOf(input)
        puzzleSolver.solvePart2()
        assertThat(puzzleSolver.result).isEqualTo(expected)
    }
}
