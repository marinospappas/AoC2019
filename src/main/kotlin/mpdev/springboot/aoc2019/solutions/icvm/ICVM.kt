package mpdev.springboot.aoc2019.solutions.icvm

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.concurrent.thread

const val DEF_PROG_THREAD = "intcode-0"

class ICVM(intCodeProgramString: String) {

    private val log: Logger = LoggerFactory.getLogger(this::class.java)

    private var program: ICProgram
    private lateinit var programThread: Thread
    private lateinit var outputThread: Thread

    private val outputValues = mutableListOf<Long>()

    init {
        program = ICProgram(intCodeProgramString)
        InputOutput.initInputOutput()
    }

    /// public functions

    fun runProgram(threadName: String = DEF_PROG_THREAD) {
        // start intcode program thread
        programThread = thread(start = true, name = threadName) {
            program.run()
        }
        log.info("IntCode Program Thread started: {} {}", programThread.name, programThread.state)
        // start output thread
        outputThread = thread(start = true, name = "output-thread-0") {   // thread that collects the output - exits when non-ascii char received
            getIntCodeOutput(outputValues)
        }
        log.info("Receive Output Thread started: {} {}", outputThread.name, programThread.state)
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

    fun getProgramOutput() = getProgramOutputLong().map { it.toInt() }

    fun getProgramOutputLong(): List<Long> {
        Thread.sleep(1)     // required in case the program thread is still in WAIT
        while (programThread.state == Thread.State.RUNNABLE) {     // game thread state WAIT = no more output
            Thread.sleep(1)
        }
        val output = outputValues.toList()
        outputValues.clear()
        log.debug("returning output: {}", output)
        return output
    }

    fun programIsRunning() = programThread.state != Thread.State.TERMINATED

    fun waitProgram() {
        programThread.join()
        log.info("IntCode Program Thread completed")
        outputThread.interrupt()
    }

    fun setProgramMemory(address: Int, data: Int) {
        program.setMemory(address, data)
    }

    fun getProgramMemory(address: Int) = getProgramMemoryLong(address).toInt()

    fun getProgramMemoryLong(address: Int) = program.getMemory(address)

    fun setLimitedMemory() {
        program.setLimitedMemory()
    }

    /// private / internal functions
    private fun getIntCodeOutput(outputValues: MutableList<Long>) {
        // runs in a loop in a separate thread wait-ing for output until interrupted
        try {
            while (true) {
                outputValues.addAll(InputOutput.getOutputValues())
            }
        } catch (e: InterruptedException) {
            log.info("Output Thread exiting")
        }
    }

}