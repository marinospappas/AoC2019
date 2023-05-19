package mpdev.springboot.aoc2019.solutions.icvm

import mpdev.springboot.aoc2019.utils.AocException
import mpdev.springboot.aoc2019.solutions.icvm.ProgramState.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class Program(prog: String) {

    private val log: Logger = LoggerFactory.getLogger(this::class.java)

    private var memory = Memory(prog)

    lateinit var threadName: String
    lateinit var inputChannel: IoChannelc
    lateinit var outputChannel: IoChannelc
    var io = InputOutput()
    var programState: ProgramState = READY
    var isIdle = false      // used in network mode only
    var quitProgram = false     // set by command "quit" when useStdio is enabled

    fun setLimitedMemory() {
        memory.unlimitedMemory = false
    }

    suspend fun run() {
        programState = RUNNING
        var ip = 0L
        while (true) {
            if (quitProgram) {
                log.info("QuitProgram command received")
                return
            }
            try {
                log.debug("program ${Thread.currentThread().name} running - ip = $ip mem ${memory[ip]}, ${memory[ip + 1]}, ${memory[ip + 2]}")
                val instruction = Instruction(ip, memory)
                log.debug("program {} - instruction {}", Thread.currentThread().name, instruction.opCode)

                when (val retCode = instruction.execute()) {
                    InstructionReturnCode.EXIT -> {
                        programState = COMPLETED
                        return
                    }
                    InstructionReturnCode.JUMP -> ip = retCode.additionalData
                    InstructionReturnCode.RELATIVE -> {
                        memory.relativeBase += retCode.additionalData
                        ip += instruction.ipIncrement
                    }
                    InstructionReturnCode.READ -> {
                        programState = WAIT
                        log.debug("IntCode instance {} waiting for input will be stored in address {}", threadName, retCode.additionalData)
                        val memAddress = retCode.additionalData     // the memory address must be saved here as this coroutine
                                                                    // will be suspended below and the value in retCode may change
                        val input = io.readInput(inputChannel)
                        setMemory(memAddress, input)
                        programState = RUNNING
                        log.debug("IntCode instance {} received input {} to be stored in address {}",
                            threadName, input, retCode.additionalData)
                        ip += instruction.ipIncrement
                        // network mode - set idle state
                        isIdle = outputChannel is NetworkChannel && input == -1L
                    }
                    InstructionReturnCode.PRINT -> {
                        log.debug("IntCode instance {} sends to output {}", threadName, retCode.additionalData)
                        io.printOutput(retCode.additionalData, outputChannel)
                        ip += instruction.ipIncrement
                    }
                    else -> ip += instruction.ipIncrement
                }
            }
            catch (e: AocException) {
                log.error("exception ${e.message} thrown, ip = $ip memory = ${memory[ip]}, ${memory[ip+1]}, ${memory[ip+2]}")
                programState = COMPLETED
                return
            }
        }
    }

    fun getMemory(address: Long): Long = memory[address]
    fun getMemory(address: Int): Long = getMemory(address.toLong())

    fun setMemory(address: Long, value: Long) {
        log.debug("setting memory {} to {}", address, value)
        memory[address] = value
    }
    fun setMemory(address: Int, value: Int) {
        setMemory(address.toLong(), value.toLong())
    }
}

class Memory(prog: String) {
    private val log: Logger = LoggerFactory.getLogger(this::class.java)

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
        if (adr == 23L)
            log.info("setting address {} to {}", adr, value)
        if (!unlimitedMemory && adr >= mem.keys.size)
            throw AocException("memory address out of range: $adr for mem size ${mem.keys.size}")
        mem[adr] = value
    }
}

enum class ProgramState {
    READY, RUNNING, WAIT, COMPLETED
}
