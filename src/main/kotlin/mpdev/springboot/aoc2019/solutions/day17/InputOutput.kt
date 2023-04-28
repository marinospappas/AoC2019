package mpdev.springboot.aoc2019.solutions.day17

import mpdev.springboot.aoc2019.utils.AocException
import mpdev.springboot.aoc2019.utils.big
import java.lang.StringBuilder
import java.math.BigInteger

object InputOutput {

    var inputIndex = 0

    var input = mutableListOf<BigInteger>()

    var output = mutableListOf<BigInteger>()

    fun readInput(): BigInteger {
        if (inputIndex < input.size)
            return input[inputIndex++]
        throw AocException("no more input")
    }

    fun setInputValues() {
        val inputString = "A,B,B,A,B,C,A,C,B,C\nL,4,L,6,L,8,L,12\nL,8,R,12,L,12\nR,12,L,6,L,6,L,8\nn\n"
        println("Input: $inputString")
        input = inputString.big()
    }

    fun printOutput() {
        println(StringBuilder().also { s -> output.forEach { n -> s.append(n.toInt().toChar()) } }
            .toString().trim('\n'))
    }
}