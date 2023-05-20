package mpdev.springboot.aoc2019.solutions.icvm

import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import mpdev.springboot.aoc2019.solutions.icvm.ProgramState.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory

abstract class AbstractICVM {

    companion object {
        const val DEF_PROG_INSTANCE_PREFIX = "intcode"
        // the ICVM "process table"
        val threadTable = mutableListOf<Program>()
    }

    protected val log: Logger = LoggerFactory.getLogger(this::class.java)

    /// protected / internal functions
    protected suspend fun runIntCodeProgram(program: Program) {
        program.run()
    }

    protected fun intCodeProgramIsRunning(program: Program) = program.programState != COMPLETED

    protected suspend fun waitIntCodeProgram(job: Job) {
        job.join()
    }

    protected fun setAsciiCapable(program: Program) {
        program.inputChannel.asciiCapable = true
        program.outputChannel.asciiCapable = true
    }

    protected fun useStdin(program: Program) {
        program.inputChannel.useStdin = true
    }

    protected suspend fun setIntCodeProgramInputLong(data: List<Long>, program: Program) {
        log.debug("set program input to {}", data)
        setInputValues(data, program.inputChannel)
    }

    protected suspend fun getIntCodeProgramOutputLong(program: Program): List<Long> {
        log.debug("getIntCodeProgramOutputLong called")
        delay(1)      // required in case the program job is still waiting for input
        while (program.programState == RUNNING) {     // job active = still producing output
            delay(1)
        }
        val output = getOutputValues(program.outputChannel)
        log.debug("returning output: {}", output)
        return output
    }

    suspend fun setInputValues(values: List<Long>, inputChannel: IoChannel = threadTable[0].inputChannel) {
        values.forEach { v -> inputChannel.data.send(v) }
    }

    suspend fun setInputValuesAscii(value: String, channel: IoChannel = threadTable[0].inputChannel) {
        setInputValues(
            mutableListOf<Long>().also { list -> value.chars().forEach { c -> list.add(c.toLong()) } },
            channel
        )
    }

    suspend fun getOutputValues(outputChannel: IoChannel = threadTable[0].outputChannel): List<Long> {
        val outputValues = mutableListOf<Long>()
        outputValues.add(outputChannel.data.receive())
        do {
            val nextItem = outputChannel.data.tryReceive().getOrNull()
            if (nextItem != null)
                outputValues.add(nextItem)
        }
        while(nextItem != null)
        return outputValues
    }

    suspend fun getOutputValuesAscii(outputChannel: IoChannel = threadTable[0].outputChannel): String {
        val outputValues = getOutputValues(outputChannel)
        return StringBuilder().also { s -> outputValues.forEach { l -> s.append(l.toInt().toChar()) } }.toString()
    }
}