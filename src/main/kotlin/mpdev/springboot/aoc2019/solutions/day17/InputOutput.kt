package mpdev.springboot.aoc2019.solutions.day17

import mpdev.springboot.aoc2019.utils.AocException
import mpdev.springboot.aoc2019.utils.big
import java.lang.StringBuilder
import java.math.BigInteger

object InputOutput {

    private var inputIndex = 0
    private var inputStringIndex = 0
    private val inputStrings = arrayOf(
        "A,B,B,A,B,C,A,C,B,C\n",
        "L,4,L,6,L,8,L,12\n",
        "L,8,R,12,L,12\n",
        "R,12,L,6,L,6,L,8\n",
        "n\n"
    )

    var input = mutableListOf<BigInteger>()

    var output = mutableListOf<BigInteger>()

    fun readInput(): BigInteger {
        if (inputIndex >= input.size)
            setInputString()
        return input[inputIndex++]
    }

    private fun setInputString() {
        printOutput()
        if (inputStringIndex < inputStrings.size) {
            val inputString = inputStrings[inputStringIndex++]
            println(inputString.trim('\n'))
            input = inputString.big()
            inputIndex = 0
        }
        else
            throw AocException("no more input")
    }

    private fun printOutput() {
        print(StringBuilder().also { s -> output.forEach { n -> s.append(n.toInt().toChar()) } }
            .toString().trim('\n'))
        output = mutableListOf()
    }
}