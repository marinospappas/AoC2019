package mpdev.springboot.aoc2019.solutions.day02

import org.springframework.stereotype.Component

@Component
class InputProcessor02 {

    fun process(inputLines: List<String>): IntCode {
        val memValuesArray = inputLines[0].split(",").map {s -> s.toInt()}.toIntArray()
        return IntCode(memValuesArray)
    }
}