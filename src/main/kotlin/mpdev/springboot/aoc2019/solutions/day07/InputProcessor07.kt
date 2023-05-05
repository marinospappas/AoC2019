package mpdev.springboot.aoc2019.solutions.day07

import org.springframework.stereotype.Component
import java.math.BigInteger

@Component
class InputProcessor07 {

    fun process(inputLines: List<String>): Program {
        val inputArray = inputLines[0].replace(" ", "").split(",")
        return Program(Array(inputArray.size) { i -> BigInteger(inputArray[i]) })
    }
}