package mpdev.springboot.aoc2019.solutions.day07

import mpdev.springboot.aoc2019.utils.AocException
import mpdev.springboot.aoc2019.solutions.day07.OpCode.*
import mpdev.springboot.aoc2019.solutions.day07.ParamReadWrite.*
import mpdev.springboot.aoc2019.solutions.day07.ParamMode.*
import mpdev.springboot.aoc2019.utils.big
import java.math.BigInteger

class Instruction(ip: Int, var memory: Memory) {

    private val opCode: OpCode
    val ipIncrement: Int
    private val params: Array<BigInteger>

    init {
        try {
            opCode = OpCode.fromValue(memory[ip].toInt())
            ipIncrement = opCode.getIpIncrement()
            params = Array(opCode.numberOfParams) { 0.big() }
            for (i in 0 until opCode.numberOfParams)
                params[i] = setInstructionParameter(ip+i, opCode.getParamMode(i), opCode.paramRwMode[i], ip)
        } catch (e: Exception) {
            throw AocException(e.message ?: "unknown exception")
        }
    }

    private fun setInstructionParameter(paramIndex: Int, paramMode: ParamMode, paramRw: ParamReadWrite, ip: Int): BigInteger {
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

    fun execute(): InstructionReturnCode {
        if (opCode == EXIT)
            return InstructionReturnCode.EXIT
        try {
            when (val result = opCode.execute(params)) {
                is BigInteger -> { store(params.last(), result); return InstructionReturnCode.OK }
                is Jump -> return InstructionReturnCode.JUMP.also { res -> res.additionalData = BigInteger.valueOf(result.newIp.toLong()) }
                is Relative -> return InstructionReturnCode.RELATIVE.also { res -> res.additionalData = result.incrBase }
            }
        }
        catch (e: AocException) {
            return InstructionReturnCode.EXIT
        }
        return InstructionReturnCode.OK
    }

    private fun fetch(address: BigInteger): BigInteger {
        if (address < 0.big())
            throw AocException("Bad memory reference:[${address}]")
        return memory[address]
    }

    private fun store(address: BigInteger, value: BigInteger) {
        if (address < BigInteger.valueOf(0L))
            throw AocException("Bad memory reference:[${address}]")
        memory[address] = value
    }
}

enum class InstructionReturnCode {
    OK,
    EXIT,
    JUMP,
    RELATIVE;

    var additionalData: BigInteger = 0.big()
}