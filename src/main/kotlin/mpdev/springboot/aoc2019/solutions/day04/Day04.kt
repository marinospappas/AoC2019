package mpdev.springboot.aoc2019.solutions.day04

import mpdev.springboot.aoc2019.model.PuzzlePartSolution
import mpdev.springboot.aoc2019.solutions.PuzzleSolver
import mpdev.springboot.aoc2019.utils.splitRepeatedChars
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import kotlin.system.measureTimeMillis

@Component
@Order(4)
class Day04: PuzzleSolver() {

    final override fun setDay() {
        day = 4         ////// update this when a puzzle solver for a new day is implemented
    }

    init {
        setDay()
    }

    var result = 0

    override fun initSolver() {}

    override fun solvePart1(): PuzzlePartSolution {
        result = 0
        val elapsed = measureTimeMillis {
            for (pwd in inputData[0].toInt() .. inputData[1].toInt())
                if (satisfiesPwdRules(pwd) { len -> len >= 2 })
                    ++result
        }
        return PuzzlePartSolution(1, result.toString(), elapsed)
    }

    override fun solvePart2(): PuzzlePartSolution {
        result = 0
        val elapsed = measureTimeMillis {
            for (pwd in inputData[0].toInt() .. inputData[1].toInt())
                if (satisfiesPwdRules(pwd) { len -> len == 2 })
                    ++result
        }
        return PuzzlePartSolution(2, result.toString(), elapsed)
    }

    fun satisfiesPwdRules(passwd: Int, repeatedChars: (Int) -> Boolean): Boolean {
        // rule 1
        val subStringsLengths = passwd.toString().splitRepeatedChars().map { s -> s.length }
        if (subStringsLengths.none { repeatedChars(it) })
            return false
        // rule 2
        var prev = 0
        return passwd.toString().chars().filter { c -> val ok = c >= prev; prev = c; ok }.count() == 6L
    }
}