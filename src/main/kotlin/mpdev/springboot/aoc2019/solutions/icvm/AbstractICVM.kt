package mpdev.springboot.aoc2019.solutions.icvm

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.concurrent.thread

open class AbstractICVM {

    companion object {
        const val DEF_PROG_THREAD = "intcode-0"
    }

    private val log: Logger = LoggerFactory.getLogger(this::class.java)

    /// protected / internal functions
    protected fun runIntCodeProgram(threadName: String, program: ICProgram) {
        // start intcode program thread
        program.intCodeThread = thread(start = true, name = threadName) {
            program.run()
        }
        log.info("IntCode Program Thread started: {} {}", program.intCodeThread.name, program.intCodeThread.state)
        // start output thread
        program.outputThread = thread(start = true, name = "output-thread-0") {   // thread that collects the output - exits when non-ascii char received
            getIntCodeOutput(program.outputValues)
        }
        log.info("Receive Output Thread started: {} {}", program.outputThread.name, program.intCodeThread.state)
    }

    protected fun intCodeProgramIsRunning(program: ICProgram) = program.intCodeThread.state != Thread.State.TERMINATED

    protected fun waitIntCodeProgram(program: ICProgram) {
        program.intCodeThread.join()
        log.info("IntCode Program Thread completed")
        program.outputThread.interrupt()
    }

    protected fun getIntCodeProgramOutputLong(program: ICProgram): List<Long> {
        Thread.sleep(1)     // required in case the program thread is still in WAIT
        while (program.intCodeThread.state == Thread.State.RUNNABLE) {     // game thread state WAIT = no more output
            Thread.sleep(1)
        }
        val output = program.outputValues.toList()
        program.outputValues.clear()
        log.debug("returning output: {}", output)
        return output
    }

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