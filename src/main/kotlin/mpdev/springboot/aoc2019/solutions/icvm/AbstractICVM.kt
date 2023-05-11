package mpdev.springboot.aoc2019.solutions.icvm

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.concurrent.thread

open class AbstractICVM {

    companion object {
        const val DEF_PROG_THREAD = "intcode-0"
    }

    protected val log: Logger = LoggerFactory.getLogger(this::class.java)

    /// protected / internal functions
    protected fun runIntCodeProgram(threadName: String, program: ICProgram) {
        // start IntCode program thread
        program.intCodeThread = thread(start = true, name = threadName) {
            program.run()
        }
        log.info("IntCode Program Thread started: {} {}", program.intCodeThread.name, program.intCodeThread.state)
    }

    protected fun intCodeProgramIsRunning(program: ICProgram) = program.intCodeThread.state != Thread.State.TERMINATED

    protected fun waitIntCodeProgram(program: ICProgram) {
        program.intCodeThread.join()
        log.info("IntCode Program Thread completed")
    }

    fun setIntCodeProgramInputLong(data: List<Long>, program: ICProgram) {
        log.debug("set program input to {}", data)
        InputOutput.setInputValues(data, program.inputChannelId)
    }

    protected fun getIntCodeProgramOutputLong(program: ICProgram): List<Long> {
        Thread.sleep(1)     // required in case the program thread is still in WAIT
        while (program.intCodeThread.state == Thread.State.RUNNABLE) {     // game thread state WAIT = no more output
            Thread.sleep(1)
        }
        val output = InputOutput.getOutputValues(program.OutputChannelId)
        log.debug("returning output: {}", output)
        return output
    }
}