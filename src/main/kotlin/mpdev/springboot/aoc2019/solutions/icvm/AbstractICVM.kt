package mpdev.springboot.aoc2019.solutions.icvm

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.concurrent.thread

abstract class AbstractICVM {

    companion object {
        const val DEF_PROG_INSTANCE_PREFIX = "intcode-thread"
        // the ICVM "process table"
        val threadTable = mutableListOf<Program>()
    }

    protected val log: Logger = LoggerFactory.getLogger(this::class.java)

    /// protected / internal functions
    protected fun runIntCodeProgram(program: Program) {
        // start IntCode program thread
        program.intCodeThread = thread(start = true, name = program.threadName) {
            program.run()
        }
        log.info("IntCode Program Thread started: {} {}", program.intCodeThread.name, program.intCodeThread.state)
    }

    protected fun intCodeProgramIsRunning(program: Program) = program.intCodeThread.state != Thread.State.TERMINATED

    protected fun waitIntCodeProgram(program: Program) {
        program.intCodeThread.join()
        log.info("IntCode Program Thread completed")
    }

    fun setIntCodeProgramInputLong(data: List<Long>, program: Program) {
        log.debug("set program input to {}", data)
        InputOutput.setInputValues(data, program.inputChannel)
    }

    protected fun getIntCodeProgramOutputLong(program: Program): List<Long> {
        Thread.sleep(1)     // required in case the program thread is still in WAIT
        while (program.intCodeThread.state == Thread.State.RUNNABLE) {     // game thread state WAIT = no more output this time round
            Thread.sleep(1)
        }
        val output = InputOutput.getOutputValues(program.outputChannel)
        log.debug("returning output: {}", output)
        return output
    }
}