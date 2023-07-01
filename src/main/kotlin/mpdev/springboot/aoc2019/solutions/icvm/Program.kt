package mpdev.springboot.aoc2019.solutions.icvm

import mpdev.springboot.aoc2019.utils.AocException
import mpdev.springboot.aoc2019.solutions.icvm.ProgramState.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class Program(prog: String) {

    private val log: Logger = LoggerFactory.getLogger(this::class.java)

    private var memory = Memory(prog)
    private var ip = 0L

    lateinit var instanceName: String
    lateinit var inputChannel: IoChannel
    lateinit var outputChannel: IoChannel
    var programState: ProgramState = READY

    var isIdle = false      // used in network mode only
    var quitProgram = false     // set by command "quit" when useStdio is enabled

    fun setLimitedMemory() {
        memory.unlimitedMemory = false
    }

    suspend fun run() {
        programState = RUNNING
        ip = 0L
        while (true) {
            if (quitProgram) {
                log.info("QuitProgram command received")
                return
            }
            try {
                log.debug("program $instanceName running - ip = $ip mem ${memory[ip]}, ${memory[ip + 1]}, ${memory[ip + 2]}")
                val instruction = Instruction(ip, memory)

                printDebug()

                // increase IP ready for the next instruction
                ip += instruction.ipIncrement
                log.debug("program {} - instruction {}", instanceName, instruction.opCode)

                when (val retCode = instruction.execute()) {
                    InstructionReturnCode.EXIT -> {
                        programState = COMPLETED
                        return
                    }
                    InstructionReturnCode.JUMP -> {
                        ip = retCode.additionalData
                    }
                    InstructionReturnCode.RELATIVE -> {
                        memory.relativeBase += retCode.additionalData
                    }
                    InstructionReturnCode.READ -> {
                        programState = WAIT
                        log.debug("IntCode instance {} waiting for input will be stored in address {}", instanceName, retCode.additionalData)
                        val memAddress = retCode.additionalData     // the memory address must be saved here as this coroutine
                                                                    // will be suspended below and the value in retCode may change
                        val input = inputChannel.readInput()
                        setMemory(memAddress, input)
                        programState = RUNNING
                        log.debug("IntCode instance {} received input {} to be stored in address {}", instanceName, input, retCode.additionalData)
                        // network mode - set idle state
                        isIdle = outputChannel is NetworkChannel && input == -1L
                    }
                    InstructionReturnCode.PRINT -> {
                        log.debug("IntCode instance {} sends to output {}", instanceName, retCode.additionalData)
                        outputChannel.printOutput(retCode.additionalData)
                    }
                    InstructionReturnCode.OK -> {}
                }
            }
            catch (e: AocException) {
                log.error("exception ${e.message} thrown, ip = $ip memory = ${memory[ip]}, ${memory[ip+1]}, ${memory[ip+2]}")
                programState = COMPLETED
                return
            }
        }
    }

    fun printDebug() {
        println("ip = $ip, next instruction:  ${memory[ip]}, ${memory[ip+1]}, ${memory[ip+2]}, ${memory[ip+3]}")
        println("      registers: A = ${memory[9998]}, B = ${memory[9997]}, SP = ${memory[10000]}, BP = ${memory[9999]}")
        println("      stack: ${memory[10000]}, ${memory[10001]}, ${memory[10002]}, ${memory[10003]}, ${memory[10004]}, ${memory[10005]}, ${memory[10006]}, ${memory[10007]}")
        println("      heap: ${memory[100000]}, ${memory[100001]}, ${memory[100002]}, ${memory[100003]}, ${memory[100004]}, ${memory[100005]}, ${memory[100006]}, ${memory[100007]}")
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

    private var mem: MutableMap<Long,Long> = mutableMapOf()
    var relativeBase: Long = 0L
    var unlimitedMemory = true

    init {
        val progArray = prog.replace(" ", "").split(",")
        progArray.filter { it.isNotEmpty() }.indices.forEach { i -> mem[i.toLong()] = progArray[i].toLong() }
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

enum class ProgramState {
    READY, RUNNING, WAIT, COMPLETED
}
