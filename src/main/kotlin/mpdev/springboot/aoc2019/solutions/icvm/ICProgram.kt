package mpdev.springboot.aoc2019.solutions.icvm

import mpdev.springboot.aoc2019.utils.AocException
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class ICProgram(var prog: String) {

    private val log: Logger = LoggerFactory.getLogger(this::class.java)

    private var memory = Memory(prog)

    fun setLimitedMemory() {
        memory.unlimitedMemory = false
    }

    fun run() {
        var ip = 0L
        while (ip in prog.indices) {
            try {
                log.debug("program ${Thread.currentThread().name} running - ip = $ip mem ${memory[ip]}, ${memory[ip+1]}, ${memory[ip+2]}")
                val instruction: Instruction
                synchronized(this) { instruction = Instruction(ip, memory) }
                log.debug("program {} - instruction {}", Thread.currentThread().name, instruction.opCode)
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
    fun setMemory(address: Int, value: Int) {
        setMemory(address.toLong(), value.toLong())
    }
}

class Memory(prog: String) {
    var mem: MutableMap<Long,Long> = mutableMapOf()
    var relativeBase: Long = 0L
    var unlimitedMemory = true

    init {
        val progArray = prog.replace(" ", "").split(",")
        progArray.indices.forEach { i -> mem[i.toLong()] = progArray[i].toLong() }
    }
    operator fun get(adr: Long): Long {
        if (!unlimitedMemory && adr >= mem.keys.size)
            throw AocException("memory address out of range: $adr for mem size ${mem.keys.size}")
        return mem[adr] ?: 0L
    }
    operator fun set(adr: Long, value: Long) {
        if (!unlimitedMemory && adr >= mem.keys.size)
            throw AocException("memory address out of range: $adr for mem size ${mem.keys.size}")
        mem[adr] = value
    }
}
