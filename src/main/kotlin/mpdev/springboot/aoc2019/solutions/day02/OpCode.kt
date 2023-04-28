package mpdev.springboot.aoc2019.solutions.day02

import mpdev.springboot.aoc2019.utils.AocException

enum class OpCode(val value: Int, val numberOfParams: Int, val execute: (Int, Int) -> Int) {
    ADD(1, 3, { a, b -> a+b } ),
    MULT(2, 3, { a, b -> a*b }),
    EXIT(99, 0, { _,_ -> Int.MIN_VALUE });

    companion object {
        fun fromValue(value: Int) =
            when (value) {
                1 -> ADD
                2 -> MULT
                99-> EXIT
                else-> throw AocException("Invalid OpCode [${value}]")
            }
    }

    fun getIpIncrement() = numberOfParams + 1
}