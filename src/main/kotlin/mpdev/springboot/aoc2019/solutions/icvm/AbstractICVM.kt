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

    protected fun setIntCodeProgramInputLong(data: List<Long>, program: Program) {
        log.debug("set program input to {}", data)
        setInputValues(data, program.inputChannel)
    }

    protected fun getIntCodeProgramOutputLong(program: Program): List<Long> {
        Thread.sleep(1)     // required in case the program thread is still in WAIT
        while (program.intCodeThread.state == Thread.State.RUNNABLE) {     // game thread state WAIT = no more output this time round
            Thread.sleep(1)
        }
        val output = getOutputValues(program.outputChannel)
        log.debug("returning output: {}", output)
        return output
    }

    fun setInputValues(values: List<Long>, inputChannel: IoChannel = threadTable[0].inputChannel) {
        synchronized(inputChannel) {
            inputChannel.data.addAll(values)
            inputChannel.notify()
        }
    }

    fun setInputValuesAscii(value: String, channel: IoChannel = threadTable[0].inputChannel) {
        setInputValues(
            mutableListOf<Long>().also { list -> value.chars().forEach { c -> list.add(c.toLong()) } },
            channel
        )
        // TODO: implement this inside the Program class: asciiInputProvided = true
    }

    fun getOutputValues(outputChannel: IoChannel = threadTable[0].outputChannel, clearChannel: Boolean = true): List<Long> {
        val outputValues: List<Long>
        synchronized(outputChannel) {
            while (outputChannel.data.isEmpty()) {
                log.debug("getOutputValues is waiting for output")
                outputChannel.wait()
                log.debug("getOutputValues has been notified")
            }
            log.debug("getOutputValues output is available")
            log.debug("output data: {}", outputChannel.data)
            outputValues = mutableListOf<Long>().also { list -> list.addAll(outputChannel.data) }
            if (clearChannel)
                outputChannel.data.removeAll { true }
        }
        return outputValues
    }

    fun getOutputValuesAscii(outputChannel: IoChannel = threadTable[0].outputChannel, clearChannel: Boolean = true): String {
        val outputValues = getOutputValues(outputChannel, clearChannel)
        return StringBuilder().also { s -> outputValues.forEach { l -> s.append(l.toInt().toChar()) } }.toString()
    }
}