package mpdev.springboot.aoc2019.solutions.day25

import mpdev.springboot.aoc2019.utils.AocException
import mpdev.springboot.aoc2019.utils.big
import java.lang.StringBuilder
import java.math.BigInteger

object InputOutput {

    private var inputIndex = 0

    var input = mutableListOf<BigInteger>()

    var output = mutableListOf<BigInteger>()

    fun readInput(): BigInteger {
        if (inputIndex >= input.size)
            setInputString()
        return input[inputIndex++]
    }

    private fun setInputString() {
        printOutput()
        val inputString = "${readln()}\n"
        println(inputString.trim('\n'))
        input = inputString.big()
        inputIndex = 0
    }

    private fun printOutput() {
        print(StringBuilder().also { s -> output.forEach { n -> s.append(n.toInt().toChar()) } }
            .toString().trim('\n'))
        output = mutableListOf()
    }
}