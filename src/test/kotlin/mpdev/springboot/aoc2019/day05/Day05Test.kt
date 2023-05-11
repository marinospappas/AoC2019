package mpdev.springboot.aoc2019.day05

import mpdev.springboot.aoc2019.input.InputDataReader
import mpdev.springboot.aoc2019.solutions.day05.Day05
import mpdev.springboot.aoc2019.solutions.icvm.InputOutput.initIoChannel
import mpdev.springboot.aoc2019.solutions.icvm.InputOutput.getOutputValues
import mpdev.springboot.aoc2019.solutions.icvm.InputOutput.setInputValues
import mpdev.springboot.aoc2019.solutions.icvm.ICProgram
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

class Day05Test {

    private val day = 5                  ///////// Update this for a new dayN test
    private val puzzleSolver = Day05()   ///////// Update this for a new dayN test
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
    fun `Reads Input`() {
        val expected = listOf("1,9,10,3,2,3,11,0,99,30,40,50")
        println("input data: $inputLines")
        assertThat(inputLines).isEqualTo(expected)
    }

    @Test
    @Order(3)
    fun `Sets up Program object with correct memory contents`() {
        val expected = listOf(1,9,10,3,2,3,11,0,99,30,40,50)
        val program = ICProgram(inputLines[0])
        for (i in expected.indices)
            assertThat(program.getMemory(i)).isEqualTo(expected[i].toLong())
    }

    @ParameterizedTest
    @Order(5)
    @MethodSource("provideArgsToPart1Test")
    fun `Solves Part 1`(input: Int, inputLine: String, expected: Int) {
        val program = ICProgram(inputLine)
        initIoChannel()
        setInputValues(listOf(input.toLong()))
        program.run()
        assertThat(getOutputValues().size).isEqualTo(1)
        assertThat(getOutputValues().last()).isEqualTo(expected.toLong())
    }

    @ParameterizedTest
    @Order(7)
    @MethodSource("provideArgsToPart2Test")
    fun `Solves Part 2`(input: Int, inputLine: String, expected: Int) {
        val program = ICProgram(inputLine)
        initIoChannel()
        setInputValues(listOf(input.toLong()))
        program.run()
        assertThat(getOutputValues().size).isEqualTo(1)
        assertThat(getOutputValues().last()).isEqualTo(expected.toLong())
    }

    companion object {
        @JvmStatic
        fun provideArgsToPart1Test(): Stream<Arguments> = Stream.of(
            Arguments.of(10, "3,0,4,0,99", 10),
            Arguments.of(-1, "3,0,4,0,99", -1),
            Arguments.of(10, "3,0, 1,0,10,9, 4,9, 99, 0, 20", 30),
            Arguments.of(10, "3,0, 1002,0,100,9, 4,9, 99, 0, 20", 1000),
        )

        @JvmStatic
        fun provideArgsToPart2Test(): Stream<Arguments> = Stream.of(
            Arguments.of(8, "3,9, 8,9,10,9, 4,9, 99, -1,8", 1),
            Arguments.of(7, "3,9, 8,9,10,9, 4,9, 99, -1,8", 0),
            Arguments.of(7, "3,9, 7,9,10,9, 4,9, 99, -1,8", 1),
            Arguments.of(8, "3,9, 7,9,10,9, 4,9, 99, -1,8", 0),
            Arguments.of(8, "3,3, 1108,-1,8,3, 4,3, 99", 1),
            Arguments.of(9, "3,3, 1108,-1,8,3, 4,3, 99", 0),
            Arguments.of(7, "3,3, 1107,-1,8,3, 4,3, 99", 1),
            Arguments.of(8, "3,3, 1107,-1,8,3, 4,3, 99", 0),
            Arguments.of(0, "3,12, 6,12,15, 1,13,14,13, 4,13, 99, -1,0,1,9", 0),
            Arguments.of(5, "3,12, 6,12,15, 1,13,14,13, 4,13, 99, -1,0,1,9", 1),
            Arguments.of(0, "3,3, 1105,-1,9, 1101,0,0,12, 4,12, 99, 1", 0),
            Arguments.of(2, "3,3, 1105,-1,9, 1101,0,0,12, 4,12, 99, 1", 1),
            Arguments.of(8, "3,21,1008,21,8,20,1005,20,22,107,8,21,20,1006,20,31," +
                    "1106,0,36,98,0,0,1002,21,125,20,4,20,1105,1,46,104," +
                    "999,1105,1,46,1101,1000,1,20,4,20,1105,1,46,98,99", 1000),
            Arguments.of(7, "3,21,1008,21,8,20,1005,20,22,107,8,21,20,1006,20,31," +
                    "1106,0,36,98,0,0,1002,21,125,20,4,20,1105,1,46,104," +
                    "999,1105,1,46,1101,1000,1,20,4,20,1105,1,46,98,99", 999),
            Arguments.of(9, "3,21,1008,21,8,20,1005,20,22,107,8,21,20,1006,20,31," +
                    "1106,0,36,98,0,0,1002,21,125,20,4,20,1105,1,46,104," +
                    "999,1105,1,46,1101,1000,1,20,4,20,1105,1,46,98,99", 1001),
        )
    }
}
