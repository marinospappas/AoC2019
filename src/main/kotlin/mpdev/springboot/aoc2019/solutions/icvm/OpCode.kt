package mpdev.springboot.aoc2019.solutions.icvm

import mpdev.springboot.aoc2019.solutions.icvm.ParamReadWrite.*
import mpdev.springboot.aoc2019.solutions.icvm.OpResultCode.*
import mpdev.springboot.aoc2019.utils.AocException

enum class OpCode(val intValue: Int,
                  val numberOfParams: Int,
                  val paramRwMode: Array<ParamReadWrite>,
                  val execute: (Array<Long>) -> Pair<OpResultCode, LongArray>) {

    ADD(1,   3, arrayOf(R,R,W), { a -> Pair(SET_MEMORY, longArrayOf(a[2], a[0] + a[1])) }),
    MULT(2,  3, arrayOf(R,R,W), { a -> Pair(SET_MEMORY, longArrayOf(a[2], a[0] * a[1])) }),
    IN(3,    1, arrayOf(W),     { a -> Pair(READ, longArrayOf(a[0])) }),
    OUT(4,   1, arrayOf(R),     { a -> Pair(PRINT, longArrayOf(a[0])) }),
    JIT(5,   2, arrayOf(R,R),   { a -> if (a[0] != 0L) Pair(SET_PC, longArrayOf(a[1]))
                                                            else Pair(NONE, longArrayOf()) }),
    JIF(6,   2, arrayOf(R,R),   { a -> if (a[0] == 0L) Pair(SET_PC, longArrayOf(a[1]))
                                                            else Pair(NONE, longArrayOf()) }),
    LT(7,    3, arrayOf(R,R,W), { a -> Pair(SET_MEMORY, longArrayOf(a[2], if (a[0] < a[1]) 1L else 0L)) }),
    EQ(8,    3, arrayOf(R,R,W), { a -> Pair(SET_MEMORY, longArrayOf(a[2], if (a[0] == a[1]) 1L else 0L)) }),
    REL(9,   1, arrayOf(R),     { a -> Pair(SET_REL_BASE, longArrayOf(a[0])) }),
    EXIT(99, 0, arrayOf(),      { _ -> Pair(OpResultCode.EXIT, longArrayOf()) });

    companion object {
        fun fromValue(value: Int): OpCode {
            val intOpcode = value % 100
            val opCode = OpCode.values().firstOrNull { it.intValue == intOpcode } ?:
                        throw AocException("invalid opcode: [$value]")
            var paramModes = value / 100
            opCode.paramMode = Array(opCode.numberOfParams) {
                val thisMode = ParamMode.fromIntValue(paramModes % 10)
                paramModes /= 10
                thisMode
            }
            return opCode
        }
    }

    private lateinit var paramMode: Array<ParamMode>

    fun getIpIncrement() = (numberOfParams + 1).toLong()

    fun getParamMode(indx: Int) = paramMode[indx]
}

enum class ParamMode(val intValue: Int) {
    POSITION(0), IMMEDIATE(1), RELATIVE(2), NONE(Int.MAX_VALUE);
    companion object {
        fun fromIntValue(intValue: Int): ParamMode = values().firstOrNull { it.intValue == intValue } ?: NONE
    }
}

enum class ParamReadWrite {
    R, W
}

enum class OpResultCode {
    SET_MEMORY, SET_PC, SET_REL_BASE, READ, PRINT, EXIT, NONE
}