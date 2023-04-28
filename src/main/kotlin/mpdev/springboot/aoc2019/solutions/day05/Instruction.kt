package mpdev.springboot.aoc2019.solutions.day05

import mpdev.springboot.aoc2019.utils.AocException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.lang.IndexOutOfBoundsException

class Instruction(ip: Int, private val mem: IntArray) {

    private val opCode: OpCode
    val ipIncrement: Int
    private val params: IntArray

    companion object {
        private val log: Logger = LoggerFactory.getLogger(this::class.java)
    }

    init {
        try {
            opCode = OpCode.fromValue(mem[ip])
            ipIncrement = opCode.getIpIncrement()
            params = IntArray(opCode.numberOfParams)
            for (i in 0 until opCode.numberOfParams) {
                val mode = opCode.getParamMode(i)
                params[i] = if (mode == 1)
                    mem[ip + i+1]           // direct parameter (value)
                else
                    fetch(mem[ip + i+1])    // positional parameter (memory address)
            }
        } catch (e: Exception) {
            throw AocException(e.message?:"unknown exception")
        }
    }

    fun execute(): InstructionReturnCode {
        if (opCode == OpCode.EXIT)
            return InstructionReturnCode.EXIT
        try {
            when (val result = opCode.execute(params)) {
                is Int -> { store(params.last(), result); return InstructionReturnCode.OK }
                is Jump -> return InstructionReturnCode.JUMP.also { res -> res.additionalData = result.newIp }
            }
        }
        catch (e: AocException) {
            return InstructionReturnCode.EXIT
        }
        return InstructionReturnCode.OK
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

enum class InstructionReturnCode {
    OK,
    EXIT,
    JUMP;

    var additionalData = 0
}