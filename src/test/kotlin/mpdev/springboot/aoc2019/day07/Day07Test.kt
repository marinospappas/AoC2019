package mpdev.springboot.aoc2019.day07

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import mpdev.springboot.aoc2019.input.InputDataReader
import mpdev.springboot.aoc2019.solutions.day07.Day07
import mpdev.springboot.aoc2019.solutions.icvm.ICVMMultipleInstances
import mpdev.springboot.aoc2019.solutions.icvm.IOMode
import mpdev.springboot.aoc2019.utils.AocUtils
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

class Day07Test {

    private val day = 7                  ///////// Update this for a new dayN test
    private val puzzleSolver = Day07()   ///////// Update this for a new dayN test
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
        // IntCode program: output = 10 * input1 + input2
        val icvm = ICVMMultipleInstances("3,0,3,1,1002,0,10,2,1,2,1,2,4,2,99")
        repeat(4) { _ -> icvm.cloneInstance(IOMode.PIPE)}
        val result: List<Int>
        runBlocking {
            // prepare the inputs
            icvm.setInstanceInput(listOf(1, 5), 0)
            icvm.setInstanceInput(listOf(2), 1)
            icvm.setInstanceInput(listOf(3), 2)
            icvm.setInstanceInput(listOf(4), 3)
            icvm.setInstanceInput(listOf(5), 4)
            // execute the 5 copies of the intCode program in 5 threads
            val jobs = Array(5) { launch { icvm.runInstance(it) } }
            // and wait until all complete
            repeat(5) { icvm.waitInstance(it, jobs[it]) }
            result = icvm.getInstanceOutput(4)
        }
        assertThat(result.size).isEqualTo(1)
        assertThat(result[0]).isEqualTo(155)
    }

    @ParameterizedTest
    @Order(3)
    @MethodSource("provideArgsToTotalThrustTest1")
    fun `Calculates Total Thrust correctly - Part1`(input: List<String>, phaseSequence: List<Int>, expected: Long) {
        puzzleSolver.inputData = input
        assertThat(puzzleSolver.calculateTotalThrust(phaseSequence)).isEqualTo(expected)
    }

    @Test
    @Order(4)
    fun `Threads in a Pipeline with Feedback Loop Pass Output to Next Thread Input`() {
        // IntCode program1: output1 = 10 * input1 + input2, output2 = 10 * input3
        val icvm = ICVMMultipleInstances("3,0,3,1,1002,0,10,2,1,2,1,2,4,2,3,0,1002,0,10,1,4,1,99")
        // IntCode program2: output = 10 * input1 + input2
        val program2 = "3,0,3,1,1002,0,10,2,1,2,1,2,4,2,99"
        repeat(2) { _ -> icvm.addInstance(program2, IOMode.PIPE)}
        icvm.addInstance(program2, IOMode.PIPE, loop = true)
        val result: List<Int>
        runBlocking {
            // prepare the inputs
            icvm.setInstanceInput(listOf(1, 5), 0)
            icvm.setInstanceInput(listOf(2), 1)
            icvm.setInstanceInput(listOf(3), 2)
            icvm.setInstanceInput(listOf(4), 3)
            // execute the 5 copies of the intCode program in 5 threads
            val jobs = Array(4) { launch { icvm.runInstance(it) } }
            // and wait until all complete
            repeat(4) { icvm.waitInstance(it, jobs[it]) }
            result = icvm.getInstanceOutput(0)
        }
        assertThat(result.size).isEqualTo(1)
        assertThat(result[0]).isEqualTo(1050)
    }

    @ParameterizedTest
    @Order(5)
    @MethodSource("provideArgsToTotalThrustTest2")
    fun `Calculates Total Thrust correctly - Part2`(input: List<String>, phaseSequence: List<Int>, expected: Long) {
        puzzleSolver.inputData = input
        assertThat(puzzleSolver.calculateTotalThrust(phaseSequence, true)).isEqualTo(expected)
    }

    @Test
    fun testPermutations() {
        val perms = AocUtils.permutations(mutableListOf(0,1,2,3,4))
        assertThat(perms.size).isEqualTo(120)
        println(perms)
    }

    companion object {

        @JvmStatic
        fun provideArgsToTotalThrustTest1(): Stream<Arguments> = Stream.of(
            Arguments.of(
                listOf("3,15,3,16,1002,16,10,16,1,16,15,15,4,15,99,0,0"),
                listOf(4,3,2,1,0), 43210L),
            Arguments.of(
                listOf("3,23,3,24,1002,24,10,24,1002,23,-1,23,101,5,23,23,1,24,23,23,4,23,99,0,0"),
                listOf(0,1,2,3,4), 54321L),
            Arguments.of(
                listOf("3,31,3,32,1002,32,10,32,1001,31,-2,31,1007,31,0,33,1002,33,7,33,1,33,31,31,1,32,31,31,4,31,99,0,0,0"),
                listOf(1,0,4,3,2), 65210L),
        )

        @JvmStatic
        fun provideArgsToTotalThrustTest2(): Stream<Arguments> = Stream.of(
            Arguments.of(
                listOf("3,26,1001,26,-4,26,3,27,1002,27,2,27,1,27,26,27,4,27,1001,28,-1,28,1005,28,6,99,0,0,5"),
                listOf(9,8,7,6,5),139629729L),
            Arguments.of(
                listOf("3,52,1001,52,-5,52,3,53,1,52,56,54,1007,54,5,55,1005,55,26,1001,54,-5,54,1105,1,12,1,53,54,53,1008,54,0,55,1001,55,1,55,2,53,55,53,4,53,1001,56,-1,56,1005,56,6,99,0,0,0,0,10"),
                listOf(9,7,8,5,6), 18216L),
        )
    }
}
