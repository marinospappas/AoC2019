package mpdev.springboot.aoc2019.solutions.icvm

import org.slf4j.Logger
import org.slf4j.LoggerFactory

open class ICVM(intCodeProgramString: String): AbstractICVM() {

    protected val log: Logger = LoggerFactory.getLogger(this::class.java)

    protected var program: ICProgram

    init {
        program = ICProgram(intCodeProgramString)
        InputOutput.initIoChannels()
    }

    /// public functions

    fun runProgram(threadName: String = DEF_PROG_THREAD) {
        runIntCodeProgram(threadName, program)
    }

    fun setProgramInput(data: Int, channelId: Int = 0) {
        setProgramInputLong(listOf(data.toLong()), channelId)
    }

    fun setProgramInput(data: List<Int>, channelId: Int = 0) {
        setProgramInputLong(data.map { it.toLong() }, channelId)
    }

    fun setProgramInputLong(data: List<Long>, channelId: Int = 0) {
        log.debug("set program input to {}", data)
        InputOutput.setInputValues(data, channelId)
    }

    fun getProgramOutput() = getIntCodeProgramOutputLong(program).map { it.toInt() }

    fun getProgramOutputLong() = getIntCodeProgramOutputLong(program)

    fun programIsRunning() = intCodeProgramIsRunning(program)

    fun waitProgram() {
        waitIntCodeProgram(program)
    }

    fun setProgramMemory(address: Int, data: Int) {
        program.setMemory(address, data)
    }

    fun getProgramMemory(address: Int) = getProgramMemoryLong(address).toInt()

    fun getProgramMemoryLong(address: Int) = program.getMemory(address)

    fun setLimitedMemory() {
        program.setLimitedMemory()
    }
}