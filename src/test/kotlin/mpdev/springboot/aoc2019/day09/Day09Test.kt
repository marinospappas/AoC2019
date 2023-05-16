package mpdev.springboot.aoc2019.day09

import mpdev.springboot.aoc2019.input.InputDataReader
import mpdev.springboot.aoc2019.solutions.day09.Day09
import mpdev.springboot.aoc2019.solutions.icvm.InputOutput
import mpdev.springboot.aoc2019.solutions.icvm.Program
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream
import kotlin.concurrent.thread

class Day09Test {

    private val day = 9                     ///////// Update this for a new dayN test
    private val puzzleSolver = Day09()      ///////// Update this for a new dayN test
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
        val program = Program(inputLines[0])
        for (i in expected.indices)
            assertThat(program.getMemory(i)).isEqualTo(expected[i].toLong())
    }
/*
    @ParameterizedTest
    @Order(5)
    @MethodSource("provideArgsToPart1Test")
    fun `Solves Part 1`(input: Int, inputLine: String, expected: List<Long>) {
        val program = Program(inputLine)
        InputOutput.initIoChannel()
        InputOutput.setInputValues(listOf(input.toLong()))
        thread(start = true, name = "self-test-0") {    // when input/output is required the intCode must run in a separate thread
            program.run()
        }.join()
        val result = InputOutput.getOutputValues()
        println(result)
        assertThat(result).isEqualTo(expected)
    }


 */
    companion object {
        @JvmStatic
        fun provideArgsToPart1Test(): Stream<Arguments> = Stream.of(
            Arguments.of(0, "109,-1, 4,1, 99", listOf<Long>(-1)),
            Arguments.of(0, "109,-1, 104,1, 99", listOf<Long>(1)),
            Arguments.of(0, "109,-1, 204,1, 99", listOf<Long>(109)),
            Arguments.of(0, "109,1, 9,2, 204,-6, 99", listOf<Long>(204)),
            Arguments.of(0, "109,1, 109,9, 204,-6, 99", listOf<Long>(204)),
            Arguments.of(0, "109,1, 209,-1, 204,-106, 99", listOf<Long>(204)),
            Arguments.of(852, "109,1, 3,3, 204, 2,99", listOf<Long>(852)),
            Arguments.of(9824, "109,1, 203,2, 204, 2,99", listOf<Long>(9824)),
            Arguments.of(10, "3,100, 4,100, 99", listOf<Long>(10)),
            Arguments.of(10, "109,1, 203,1000, 4,1001, 99", listOf<Long>(10)),
            Arguments.of(0, "1102,34915192,34915192,7,4,7,99,0", listOf(1219070632396864)),
            Arguments.of(0, "104,9125899906842624158,99", listOf(9125899906842624158)),
            Arguments.of(0, "109,1, 204,-1, 1001,100,1,100, 1008,100,16, 101,1006,101,0, 99",
                listOf<Long>(109, 1, 204, -1, 1001, 100, 1, 100, 1008, 100, 16, 101, 1006, 101, 0, 99)),
        )
    }
}
