package mpdev.springboot.aoc2019.solutions.icvm

import mpdev.springboot.aoc2019.utils.AocException
import mpdev.springboot.aoc2019.utils.big
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.math.BigInteger

class Program(var prog: String) {

    companion object {
        private val log: Logger = LoggerFactory.getLogger(this::class.java)
    }

    private var memory = Memory(prog)

    fun run() {
        var ip = 0
        //memory = Memory(prog)
        while (ip in prog.indices) {
            try {
                log.info("program ${Thread.currentThread().name} running - ip = $ip mem ${memory[ip]}, ${memory[ip+1]}, ${memory[ip+2]}")
                val instruction: Instruction
                synchronized(this) { instruction = Instruction(ip, memory) }
                log.info("program ${Thread.currentThread().name} - instruction ${instruction.opCode}")
                synchronized(this) {
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
            }
            catch (e: AocException) {
                log.error("exception ${e.message} thrown, ip = $ip memory = ${memory[ip]}, ${memory[ip+1]}, ${memory[ip+2]}")
                return
            }
        }
    }

    fun getMemory(address: Int): Int = getMemory(address.big()).toInt()
    fun getMemory(address: BigInteger): BigInteger = memory[address]

    fun setMemory(address: Int, value: Int) {
        setMemory(address.big(), value.big())
    }
    fun setMemory(address: BigInteger, value: BigInteger) {
        memory[address] = value
    }
}

class Memory(prog: String) {
    var mem: MutableMap<BigInteger,BigInteger> = mutableMapOf()
    var relativeBase: BigInteger = 0.big()
    init {
        val progArray = prog.split(",")
        progArray.indices.forEach { i -> mem[i.big()] = BigInteger(progArray[i]) }
    }
    operator fun get(i: BigInteger): BigInteger = mem[i] ?: 0.big()
    operator fun get(i: Int): BigInteger = mem[i.big()] ?: 0.big()
    operator fun set(i: BigInteger, value: BigInteger) { mem[i] = value }
}