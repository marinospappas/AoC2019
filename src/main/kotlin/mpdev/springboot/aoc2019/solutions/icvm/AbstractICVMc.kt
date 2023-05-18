package mpdev.springboot.aoc2019.solutions.icvm

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import org.slf4j.Logger
import org.slf4j.LoggerFactory

abstract class AbstractICVMc {

    companion object {
        const val DEF_PROG_INSTANCE_PREFIX = "intcode-thread"
        // the ICVM "process table"
        val threadTable = mutableListOf<Programc>()
    }

    protected val log: Logger = LoggerFactory.getLogger(this::class.java)

    /// protected / internal functions
    protected suspend fun runIntCodeProgram(program: Programc) {
        delay(10)
        program.run()
        log.info("IntCode Program Co-routine started") // {}", program.job.isActive)
    }

    protected fun intCodeProgramIsRunning(program: Programc) = program.job.isActive

    protected suspend fun waitIntCodeProgram(program: Programc) {
        program.job.join()
        log.info("IntCode Program Thread completed")
    }

    protected suspend fun setIntCodeProgramInputLong(data: List<Long>, program: Programc) {
        log.debug("set program input to {}", data)
        setInputValues(data, program.inputChannel)
    }

    protected suspend fun getIntCodeProgramOutputLong(program: Programc): List<Long> {
        log.debug("getIntCodeProgramOutputLong called")
        delay(1)      // required in case the program job is still waiting for input
        while (program.job.isActive) {     // job active = still producing output
            delay(1)
        }
        val output = getOutputValues(program.outputChannel)
        log.debug("returning output: {}", output)
        return output
    }

    suspend fun setInputValues(values: List<Long>, inputChannel: Channel<Long> = threadTable[0].inputChannel) {
        values.forEach { v -> inputChannel.send(v) }
    }

    suspend fun setInputValuesAscii(value: String, channel: Channel<Long> = threadTable[0].inputChannel) {
        setInputValues(
            mutableListOf<Long>().also { list -> value.chars().forEach { c -> list.add(c.toLong()) } },
            channel
        )
        // TODO: implement this inside the Program class:
        //  asciiInputProvided = true
    }

    suspend fun getOutputValues(outputChannel: Channel<Long> = threadTable[0].outputChannel, clearChannel: Boolean = true): List<Long> {
        val outputValues = mutableListOf<Long>()
        outputValues.add(outputChannel.receive())
        do {
            val nextItem = outputChannel.tryReceive().getOrNull()
            if (nextItem != null)
                outputValues.add(nextItem)
        }
        while(nextItem != null)
        return outputValues
    }

    //TODO fix below
    //fun getOutputValuesAscii(outputChannel: IoChannel = threadTable[0].outputChannel, clearChannel: Boolean = true): String {
    //    val outputValues = getOutputValues(outputChannel, clearChannel)
    //    return StringBuilder().also { s -> outputValues.forEach { l -> s.append(l.toInt().toChar()) } }.toString()
    //}
}