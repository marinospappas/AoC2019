package mpdev.springboot.aoc2019.solutions.day25

import mpdev.springboot.aoc2019.utils.AocException
import mpdev.springboot.aoc2019.utils.big
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.math.BigInteger

class Program(private val prog: Array<BigInteger>) {

    companion object {
        private val log: Logger = LoggerFactory.getLogger(this::class.java)
    }

    private var memory = Memory(prog)
    private var ip = 0

    fun run() {
        initProgram()
        try {
            while (ip in prog.indices) {
                val instruction = Instruction(ip, memory)
                when (val retCode = instruction.execute()) {
                    InstructionReturnCode.EXIT -> return
                    InstructionReturnCode.JUMP -> ip = retCode.additionalData.toInt()
                    InstructionReturnCode.RELATIVE -> {
                        memory.relativeBase += retCode.additionalData
                        ip += instruction.ipIncrement
                    }
                    else -> ip += instruction.ipIncrement
                }
            }
        } catch (e: AocException) {
            log.error("exception thrown: ${e.message}, ip = $ip")
            return
        }
    }

    private fun initProgram() {
        ip = 0
        memory = Memory(prog)
        InputOutput.output = mutableListOf()
    }
}

class Memory(prog: Array<BigInteger>) {
    var mem: MutableMap<BigInteger,BigInteger> = mutableMapOf()
    var relativeBase: BigInteger = 0.big()
    init {
        prog.indices.forEach { i -> mem[i.big()] = prog[i] }
    }
    operator fun get(i: BigInteger): BigInteger = mem[i] ?: 0.big()
    operator fun get(i: Int): BigInteger = mem[i.big()] ?: 0.big()
    operator fun set(i: BigInteger, value: BigInteger) { mem[i] = value }
}