package mpdev.springboot.aoc2019.day02

import mpdev.springboot.aoc2019.input.InputDataReader
import mpdev.springboot.aoc2019.solutions.day02.Day02
import mpdev.springboot.aoc2019.solutions.icvm.OpCode
import mpdev.springboot.aoc2019.solutions.icvm.Program
import mpdev.springboot.aoc2019.utils.AocException
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import kotlin.system.measureTimeMillis

class Day02Test {

    private val day = 2                       ///////// Update this for a new dayN test
    private val puzzleSolver = Day02()        ///////// Update this for a new dayN test
    private val inputDataReader = InputDataReader("src/test/resources/inputdata/input")
    private var inputLines: List<String> = inputDataReader.read(day)
    private var partResult = 0

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
    fun `Reads Input`() {
        val expected = listOf("1,9,10,3,2,3,11,0,99,30,40,50")
        println("input data: $inputLines")
        assertThat(inputLines).isEqualTo(expected)
    }

    @Test
    @Order(3)
    fun `Sets up IntCode object`() {
        val expected = listOf(1,9,10,3,2,3,11,0,99,30,40,50)
        val program = Program(inputLines[0])
        for (i in expected.indices)
            assertThat(program.getMemory(i)).isEqualTo(expected[i])
    }

    @ParameterizedTest
    @ValueSource(ints = [97, 98, 100, 2000])
    @Order(4)
    fun `Invalid opcode throws exception`(code: Int) {
        assertThrows<AocException> { OpCode.fromValue(code) }
    }

    @Test
    @Order(5)
    fun `Solves Part 1`() {
        val expected = 3500
        val program = Program(inputLines[0])
        val elapsed = measureTimeMillis { program.run() }
        println("elapsed time part1: $elapsed  msec")
        assertThat(program.getMemory(0)).isEqualTo(expected)
    }

    @Test
    @Order(6)
    fun `Solves Part 2`() {
        val expected = 910
        puzzleSolver.part2Output = 3500
        val elapsed = measureTimeMillis { partResult = puzzleSolver.solvePart2().result.toInt() }
        println("elapsed time part2: $elapsed  msec")
        assertThat(partResult).isEqualTo(expected) }
}
