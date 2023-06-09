package mpdev.springboot.aoc2019.solutions.icvm

import mpdev.springboot.aoc2019.solutions.icvm.ParamReadWrite.*

enum class OpCode(val intValue: Int,
                  val numberOfParams: Int,
                  val paramRwMode: Array<ParamReadWrite>,
                  val execute: (Array<Long>) -> Any) {

    ADD(1,   3, arrayOf(R,R,W), { a -> a[0] + a[1] }),
    MULT(2,  3, arrayOf(R,R,W), { a -> a[0] * a[1] }),
    IN(3,    1, arrayOf(W),     { a -> Read(a[0]) }),
    OUT(4,   1, arrayOf(R),     { a -> Print(a[0]) }),
    JIT(5,   2, arrayOf(R,R),   { a -> if (a[0] != 0L) Jump(a[1]) else Unit }),
    JIF(6,   2, arrayOf(R,R),   { a -> if (a[0] == 0L) Jump(a[1]) else Unit }),
    LT(7,    3, arrayOf(R,R,W), { a -> if (a[0] < a[1]) 1L else 0L }),
    EQ(8,    3, arrayOf(R,R,W), { a -> if (a[0] == a[1]) 1L else 0L }),
    REL(9,   1, arrayOf(R),     { a -> Relative(a[0]) }),
    EXIT(99, 0, arrayOf(),      { _ -> Int.MIN_VALUE });

    companion object {
        fun fromValue(value: Int): OpCode {
            val intOpcode = value % 100
            val opCode = OpCode.values().first { it.intValue == intOpcode }
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
        fun fromIntValue(intValue: Int): ParamMode =
            if (values().any { it.intValue == intValue })
                values().first { it.intValue == intValue }
            else
                NONE
    }
}

enum class ParamReadWrite {
    R, W
}

class Jump(val newIp: Long)

class Relative(val incrBase: Long)

class Read(address: Long)

class Print(address: Long)