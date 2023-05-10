package mpdev.springboot.aoc2019.solutions.icvm

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.concurrent.thread

const val DEF_PROG_THREAD = "intcode-0"
class ICVM(intCodeProgramString: String, numberOfThreads: Int = 1) {

    private val log: Logger = LoggerFactory.getLogger(this::class.java)

    private var program: ICProgram
    private lateinit var programThread: Thread
    private lateinit var outputThread: Thread

    private val outputValues = mutableListOf<Long>()

    init {
        program = ICProgram(intCodeProgramString)
        InputOutput.initInputOutput(numberOfThreads)
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

    fun getProgramOutput(): List<Int> {
        Thread.sleep(1)     // required in case the program thread is still in WAIT
        while (programThread.state == Thread.State.RUNNABLE) {     // game thread state WAIT = no more output
            Thread.sleep(1)
        }
        if (programThread.state == Thread.State.TERMINATED) {
            log.info("IntCOde Program Thread state: {}", programThread.state)
            return emptyList()
        }
        val output = outputValues.toList().map { it.toInt() }
        outputValues.clear()
        log.debug("returing output: {}", output)
        return output
    }

    fun waitProgram() {
        programThread.join()
        log.info("IntCode Program Thread completed")
        outputThread.interrupt()
    }

    /// private / internal functions
    private fun getIntCodeOutput(outputValues: MutableList<Long>) {
        // runs in a loop in a separate thread until interrupted
        try {
            while (true) {
                outputValues.addAll(InputOutput.getOutputValues())
            }
        } catch (e: InterruptedException) {
            log.info("Output Thread exiting")
        }
    }

}