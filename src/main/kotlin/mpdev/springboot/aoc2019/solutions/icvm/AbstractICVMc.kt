package mpdev.springboot.aoc2019.solutions.icvm

import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import mpdev.springboot.aoc2019.solutions.icvm.ProgramState.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory

abstract class AbstractICVMc {

    companion object {
        const val DEF_PROG_INSTANCE_PREFIX = "intcode"
        // the ICVM "process table"
        val threadTable = mutableListOf<Programc>()
    }

    protected val log: Logger = LoggerFactory.getLogger(this::class.java)

    /// protected / internal functions
    protected suspend fun runIntCodeProgram(program: Programc) {
        program.run()
    }

    protected fun intCodeProgramIsRunning(program: Programc) = program.programState != COMPLETED

    protected suspend fun waitIntCodeProgram(job: Job) {
        job.join()
    }

    protected suspend fun setIntCodeProgramInputLong(data: List<Long>, program: Programc) {
        log.debug("set program input to {}", data)
        setInputValues(data, program.inputChannel)
    }

    protected suspend fun getIntCodeProgramOutputLong(program: Programc): List<Long> {
        log.debug("getIntCodeProgramOutputLong called")
        delay(1)      // required in case the program job is still waiting for input
        while (program.programState == RUNNING) {     // job active = still producing output
            delay(1)
        }
        val output = getOutputValues(program.outputChannel)
        log.debug("returning output: {}", output)
        return output
    }

    suspend fun setInputValues(values: List<Long>, inputChannel: IoChannelc = threadTable[0].inputChannel) {
        values.forEach { v -> inputChannel.data.send(v) }
    }

    suspend fun setInputValuesAscii(value: String, channel: IoChannelc = threadTable[0].inputChannel) {
        setInputValues(
            mutableListOf<Long>().also { list -> value.chars().forEach { c -> list.add(c.toLong()) } },
            channel
        )
        // TODO: implement this inside the Program class:
        //  asciiInputProvided = true
    }

    suspend fun getOutputValues(outputChannel: IoChannelc = threadTable[0].outputChannel): List<Long> {
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

    suspend fun getOutputValuesAscii(outputChannel: IoChannelc = threadTable[0].outputChannel): String {
        val outputValues = getOutputValues(outputChannel)
        return StringBuilder().also { s -> outputValues.forEach { l -> s.append(l.toInt().toChar()) } }.toString()
    }
}