package mpdev.springboot.aoc2019.day07

import mpdev.springboot.aoc2019.input.InputDataReader
import mpdev.springboot.aoc2019.solutions.day07.Day07CpuV3
import mpdev.springboot.aoc2019.solutions.day07.InputOutput
import mpdev.springboot.aoc2019.solutions.day07.InputProcessor07
import mpdev.springboot.aoc2019.utils.AocUtils
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.math.BigInteger
import java.util.stream.Stream
import kotlin.concurrent.thread

class Day07Test {

    private val day = 7                                     ///////// Update this for a new dayN test
    private val inputProcessor = InputProcessor07()         ///////// Update this for a new dayN test
    private val puzzleSolver = Day07CpuV3(inputProcessor)   ///////// Update this for a new dayN test
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
    fun `Threads in a Pipeline Pass Output to Next Thread Input`() {
        InputOutput.initInputOutput(5)
        InputOutput.setInputValues(0, listOf(BigInteger("1"), BigInteger("5")))
        InputOutput.setInputValues(1, listOf(BigInteger("2")))
        InputOutput.setInputValues(2, listOf(BigInteger("3")))
        InputOutput.setInputValues(3, listOf(BigInteger("4")))
        InputOutput.setInputValues(4, listOf(BigInteger("5")))
        val testThreads = Array(5) {
            thread(start = true, name = "test-thread-$it") {
                readWriteInThread()
            }
        }
        testThreads.forEach { t -> t.join() }
        val result = InputOutput.getOutputValues(4)
        assertThat(result.size).isEqualTo(1)
        assertThat(result[0].toInt()).isEqualTo(155)
    }

    private fun readWriteInThread() {
        val input1 = InputOutput.readInput()
        val input2 = InputOutput.readInput()
        InputOutput.printOutput(input1 * BigInteger("10") + input2)
    }

    @ParameterizedTest
    @Order(3)
    @MethodSource("provideArgsToTotalThrustTest")
    fun `Calculates Total Thrust correctly`(input: List<String>, phaseSequence: List<Int>, expected: BigInteger) {
        puzzleSolver.inputData = input
        assertThat(puzzleSolver.calculateTotalThrust(phaseSequence)).isEqualTo(expected)
    }

    @Test
    fun testCombinations() {
        val perms = AocUtils.permutations(mutableListOf(0,1,2,3,4))
        println(perms)
    }

    companion object {
        @JvmStatic
        fun provideArgsToTotalThrustTest(): Stream<Arguments> = Stream.of(
            Arguments.of(
                listOf("3,15,3,16,1002,16,10,16,1,16,15,15,4,15,99,0,0"),
                listOf(4,3,2,1,0), BigInteger.valueOf(43210L)),
            Arguments.of(
                listOf("3,23,3,24,1002,24,10,24,1002,23,-1,23,101,5,23,23,1,24,23,23,4,23,99,0,0"),
                listOf(0,1,2,3,4), BigInteger.valueOf(54321L)),
            Arguments.of(
                listOf("3,31,3,32,1002,32,10,32,1001,31,-2,31,1007,31,0,33,1002,33,7,33,1,33,31,31,1,32,31,31,4,31,99,0,0,0"),
                listOf(1,0,4,3,2), BigInteger.valueOf(65210L)),
        )

    }
}
