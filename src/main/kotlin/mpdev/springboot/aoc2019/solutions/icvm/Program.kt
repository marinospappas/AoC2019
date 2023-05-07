package mpdev.springboot.aoc2019.solutions.icvm

import mpdev.springboot.aoc2019.utils.AocException
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class Program(var prog: String) {

    companion object {
        private val log: Logger = LoggerFactory.getLogger(this::class.java)
    }

    private var memory = Memory(prog)

    fun run() {
        var ip = 0L
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
                        InstructionReturnCode.JUMP -> ip = retCode.additionalData
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

    fun getMemory(address: Long): Long = memory[address]
    fun getMemory(address: Int): Long = getMemory(address.toLong())

    fun setMemory(address: Long, value: Long) {
        memory[address] = value
    }
    fun setMemory(address: Int, value: Long) {
        setMemory(address.toLong(), value)
    }
    fun setMemory(address: Int, value: Int) {
        setMemory(address.toLong(), value.toLong())
    }
}

class Memory(prog: String) {
    var mem: MutableMap<Long,Long> = mutableMapOf()
    var relativeBase: Long = 0L

    init {
        val progArray = prog.split(",")
        progArray.indices.forEach { i -> mem[i.toLong()] = progArray[i].toLong() }
    }
    operator fun get(adr: Long): Long = mem[adr] ?: 0L
    operator fun set(adr: Long, value: Long) { mem[adr] = value }
}
