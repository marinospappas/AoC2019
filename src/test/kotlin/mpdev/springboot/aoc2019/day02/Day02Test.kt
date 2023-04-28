package mpdev.springboot.aoc2019.day02

import mpdev.springboot.aoc2019.input.InputDataReader
import mpdev.springboot.aoc2019.solutions.day02.Day02CpuV1
import mpdev.springboot.aoc2019.solutions.day02.InputProcessor02
import mpdev.springboot.aoc2019.utils.AocException
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.system.measureTimeMillis

class Day02Test {

    private val day = 2                                     ///////// Update this for a new dayN test
    private val inputProcessor = InputProcessor02()         ///////// Update this for a new dayN test
    private val puzzleSolver = Day02CpuV1(inputProcessor)        ///////// Update this for a new dayN test
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
        val intCode = inputProcessor.process(inputLines)
        for (i in intCode.mem.indices)
            assertThat(intCode.mem[i]).isEqualTo(expected[i])
    }

    @Test
    @Order(4)
    fun `Invalid opcode throws exception`() {
        inputLines = listOf("100,101,102,103")
        val intCode = inputProcessor.process(inputLines)
        assertThrows<AocException> { intCode.run() }
    }

    @Test
    @Order(5)
    fun `Solves Part 1`() {
        val expected = 3500
        val intCode  = inputProcessor.process(inputLines)
        val elapsed = measureTimeMillis { intCode.run() }
        println("elapsed time part1: $elapsed  msec")
        assertThat(intCode.mem[0]).isEqualTo(expected)
    }


    @Test
    @Order(6)
    fun `Saves and restores mem`() {
        val expected = listOf(1,9,10,3,2,3,11,0,99,30,40,50)
        val intCode = inputProcessor.process(inputLines)
        for (i in intCode.mem.indices)
            assertThat(intCode.mem[i]).isEqualTo(expected[i])

        intCode.corruptMem()
        for (i in intCode.mem.indices)
            assertThat(intCode.mem[i]).isNotEqualTo(expected[i])

        intCode.restoreMem()
        for (i in intCode.mem.indices)
            assertThat(intCode.mem[i]).isEqualTo(expected[i])
    }

    @Test
    @Order(6)
    fun `Solves Part 2`() {
        val expected = 910
        puzzleSolver.output = 3500
        val elapsed = measureTimeMillis { partResult = puzzleSolver.solvePart2().result.toInt() }
        println("elapsed time part2: $elapsed  msec")
        assertThat(partResult).isEqualTo(expected) }
}
