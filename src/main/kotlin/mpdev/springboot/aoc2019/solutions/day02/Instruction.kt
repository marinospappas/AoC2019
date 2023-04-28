package mpdev.springboot.aoc2019.solutions.day02

import mpdev.springboot.aoc2019.utils.AocException
import java.lang.IndexOutOfBoundsException

class Instruction(ip: Int, private val mem: IntArray) {

    private val opCode: OpCode
    val ipIncrement: Int
    private val params: IntArray

    init {
        try {
            opCode = OpCode.fromValue(mem[ip])
            ipIncrement = opCode.getIpIncrement()
            params = IntArray(opCode.numberOfParams)
            for (i in 1..opCode.numberOfParams)
                params[i-1] = mem[ip+i]
        } catch (e: Exception) {
            throw AocException(e.message?:"unknown exception")
        }
    }

    fun execute(): Boolean {
        if (opCode == OpCode.EXIT)
            return false
        try {
            val result = opCode.execute(fetch(params[0]), fetch(params[1]))
            store(params[2], result)
        }
        catch (e: AocException) {
            return false
        }
        return true
    }

    private fun fetch(address: Int): Int {
        try {
            return mem[address]
        } catch (e: IndexOutOfBoundsException) {
            throw AocException("Bad memory reference:[${address}] for size [${mem.size}]")
        }
    }

    private fun store(address: Int, value: Int) {
        try {
            mem[address] = value
        } catch (e: IndexOutOfBoundsException) {
            throw AocException("Bad memory reference:[${address}] for size [${mem.size}]")
        }
    }
}
