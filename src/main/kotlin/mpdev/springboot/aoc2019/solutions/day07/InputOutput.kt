package mpdev.springboot.aoc2019.solutions.day07

import mpdev.springboot.aoc2019.utils.AocException
import java.math.BigInteger

object InputOutput {

    private var inputIndex = 0

    var input = mutableListOf<BigInteger>()

    var output = mutableListOf<BigInteger>()

    fun initInputOutput() {
        inputIndex = 0
        input = mutableListOf()
        output = mutableListOf()
    }

    fun readInput(): BigInteger {
        if (inputIndex < input.size)
            return input[inputIndex++]
        else
            throw AocException("no more input")
    }

    fun printOutput(value: BigInteger) {
        output.add(value)
    }
}