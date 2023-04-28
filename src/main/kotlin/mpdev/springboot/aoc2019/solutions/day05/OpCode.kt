package mpdev.springboot.aoc2019.solutions.day05

import mpdev.springboot.aoc2019.utils.AocException

enum class OpCode(val value: Int,
                  val numberOfParams: Int,
                  val execute: (IntArray) -> Any) {

    ADD(1, 3,   { a -> a[0]+a[1] } ),
    MULT(2, 3,  { a -> a[0]*a[1] }),
    IN(3, 1,    { _ -> InputOutput.input }),
    OUT(4, 1,   { a -> InputOutput.output.add(a[0]) }),
    JIT(5, 2,   { a -> if (a[0] != 0) Jump(a[1]) else Unit }),
    JIF(6, 2,   { a -> if (a[0] == 0) Jump(a[1]) else Unit }),
    LT(7, 3,    { a -> if (a[0] < a[1]) 1 else 0 }),
    EQ(8, 3,    { a -> if (a[0] == a[1]) 1 else 0 }),
    EXIT(99, 0, { _ -> Int.MIN_VALUE });

    companion object {
        fun fromValue(value: Int): OpCode {
            val opCode = when (value % 100) {
                1 -> ADD
                2 -> MULT
                3 -> IN
                4 -> OUT
                5 -> JIT
                6 -> JIF
                7 -> LT
                8 -> EQ
                99 -> EXIT
                else -> throw AocException("Invalid OpCode [${value}]")
            }
            opCode.paramMode = IntArray(opCode.numberOfParams)
            var modes = value / 100
            for (i in opCode.paramMode.indices) {
                opCode.paramMode[i] = modes % 10
                modes /= 10
            }
            if (!setOf(OUT,EXIT,JIT,JIF).contains(opCode))
                opCode.paramMode[opCode.paramMode.indices.last] = 1
            return opCode
        }
    }

    private var paramMode: IntArray = intArrayOf()

    fun getIpIncrement() = numberOfParams + 1

    fun getParamMode(indx: Int) = paramMode[indx]
}

class Jump(val newIp: Int)