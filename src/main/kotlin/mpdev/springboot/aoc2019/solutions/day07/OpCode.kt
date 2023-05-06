package mpdev.springboot.aoc2019.solutions.day07

import mpdev.springboot.aoc2019.utils.AocException
import mpdev.springboot.aoc2019.solutions.day07.ParamReadWrite.*
import mpdev.springboot.aoc2019.utils.big
import java.math.BigInteger

enum class OpCode(val value: Int,
                  val numberOfParams: Int,
                  val paramRwMode: Array<ParamReadWrite>,
                  val execute: (Array<BigInteger>) -> Any) {

    ADD(1,   3, arrayOf(R,R,W), { a -> a[0] + a[1] } ),
    MULT(2,  3, arrayOf(R,R,W), { a -> a[0] * a[1] }),
    IN(3,    1, arrayOf(W),     { _ -> InputOutput.readInput() }),
    OUT(4,   1, arrayOf(R),     { a -> InputOutput.printOutput(a[0]) }),
    JIT(5,   2, arrayOf(R,R),   { a -> if (a[0] != 0.big()) Jump(a[1].toInt()) else Unit }),
    JIF(6,   2, arrayOf(R,R),   { a -> if (a[0] == 0.big()) Jump(a[1].toInt()) else Unit }),
    LT(7,    3, arrayOf(R,R,W), { a -> (if (a[0] < a[1]) 1 else 0).big() }),
    EQ(8,    3, arrayOf(R,R,W), { a -> (if (a[0] == a[1]) 1 else 0).big() }),
    REL(9,   1, arrayOf(R),     { a -> Relative(a[0]) }),
    EXIT(99, 0, arrayOf(),      { _ -> Int.MIN_VALUE });

    companion object {
        fun fromValue(value: Int): OpCode = synchronized(this) {
            val opCode = when (value % 100) {
                1 -> ADD
                2 -> MULT
                3 -> IN
                4 -> OUT
                5 -> JIT
                6 -> JIF
                7 -> LT
                8 -> EQ
                9 -> REL
                99 -> EXIT
                else -> throw AocException("Invalid OpCode [${value}]")
            }
            var paramModes = value / 100
            opCode.paramMode = Array(opCode.numberOfParams) { i ->
                val thisMode = ParamMode.fromIntValue(paramModes % 10)
                paramModes /= 10
                thisMode
            }
            return opCode
        }
    }

    private lateinit var paramMode: Array<ParamMode>

    fun getIpIncrement() = numberOfParams + 1

    fun getParamMode(indx: Int) = paramMode[indx]
}

enum class ParamMode(val intValue: Int) {
    POSITION(0), IMMEDIATE(1), RELATIVE(2), NONE(Int.MAX_VALUE);

    companion object {
        fun fromIntValue(intValue: Int): ParamMode = synchronized(this) {
            return if (values().any { it.intValue == intValue })
                values().first { it.intValue == intValue }
            else
                NONE
        }
    }
}

enum class ParamReadWrite {
    R, W
}

class Jump(val newIp: Int)

class Relative(val incrBase: BigInteger)