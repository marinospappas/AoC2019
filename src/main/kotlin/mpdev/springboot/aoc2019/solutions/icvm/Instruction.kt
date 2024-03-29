package mpdev.springboot.aoc2019.solutions.icvm

import mpdev.springboot.aoc2019.utils.AocException
import mpdev.springboot.aoc2019.solutions.icvm.ParamReadWrite.*
import mpdev.springboot.aoc2019.solutions.icvm.ParamMode.*

data class Instruction(val ip: Long, var memory: Memory) {

    private val opCode: OpCode
    private val ipIncrement: Long
    private val params: Array<Long>

    init {
        try {
            opCode = OpCode.fromValue(memory[ip].toInt())
            ipIncrement = opCode.getIpIncrement()
            params = Array(opCode.numberOfParams) { 0L }
            for (i in 0 until opCode.numberOfParams)
                params[i] = setInstructionParameter(ip + i, opCode.getParamMode(i), opCode.paramRwMode[i], ip)
        } catch (e: Exception) {
            throw AocException(e.message ?: "unknown exception")
        }
    }

    private fun setInstructionParameter(paramIndex: Long, paramMode: ParamMode, paramRw: ParamReadWrite, ip: Long): Long {
        return if (paramRw == R)
            when (paramMode) {      // return the contents of the mem address or value if immediate
                POSITION -> fetch(memory[paramIndex + 1])                               // positional parameter (memory address)
                IMMEDIATE -> memory[paramIndex + 1]                                     // direct parameter (value)
                RELATIVE -> fetch(memory[paramIndex + 1] + memory.relativeBase)  // relative mode positional parameter (memory address)
                NONE -> throw AocException("invalid parameter mode [${memory[ip]}]")
            }
        else
            when (paramMode) {      // return the memory address (immediate is invalid)
                POSITION -> memory[paramIndex + 1]                        // positional parameter (memory address)
                RELATIVE -> memory[paramIndex + 1] + memory.relativeBase  // relative mode positional parameter (memory address)
                IMMEDIATE, NONE -> throw AocException("invalid parameter mode [${memory[ip]}]")
            }
    }

    fun get() = Triple(opCode, params, ipIncrement)

    private fun fetch(address: Long): Long {
        if (address < 0L)
            throw AocException("Bad memory reference:[${address}]")
        return memory[address]
    }
}