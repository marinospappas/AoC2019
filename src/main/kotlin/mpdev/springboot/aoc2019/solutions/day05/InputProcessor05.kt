package mpdev.springboot.aoc2019.solutions.day05

import org.springframework.stereotype.Component

@Component
class InputProcessor05 {

    fun process(inputLines: List<String>): Program {
        val memValuesArray = inputLines[0].replace(" ", "").split(",").map {s -> s.toInt()}.toIntArray()
        return Program(memValuesArray)
    }
}