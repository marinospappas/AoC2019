package mpdev.springboot.aoc2019.solutions.icvm

import mpdev.springboot.aoc2019.utils.AocException
import mpdev.springboot.aoc2019.solutions.icvm.IoMode.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object InputOutput {

    private val log: Logger = LoggerFactory.getLogger(this::class.java)

    private var inputChannels = arrayOf<IoChannel>()

    private var outputChannels = arrayOf<IoChannel>()

    private var useStdout = false
    private var useStdin = false

    fun initInputOutput(numThreads: Int = 1, loop: Boolean = false, stdout: Boolean = false, stdin: Boolean = false) {
        useStdout = stdout
        useStdin = stdin
        if (!loop) {
            inputChannels = Array(numThreads) { i ->
                when (i) {
                    0 -> IoChannel()
                    else -> IoChannel(mode = PIPE)
                }
            }
            outputChannels = Array(numThreads) { i ->
                when (i) {
                    numThreads-1 -> IoChannel()
                    else -> inputChannels[i+1]
                }
            }
            log.info("initialised $numThreads io channels")
        }
        else {
            outputChannels = Array(numThreads) { i -> IoChannel(mode = PIPE) }
            inputChannels = Array(numThreads) { i ->
                when (i) {
                    0 -> outputChannels.last()
                    else -> outputChannels[i-1]
                }
            }
            log.info("initialised $numThreads io channels with feedback loop")
        }
    }

    fun setInputValues(values: List<Long>, channel: Int = 0) {
        inputChannels[channel].data.addAll(values)
        inputChannels[channel].syncObject.dataReady = true
        log.info("set input values for channel [$channel] to ${inputChannels[channel].data}")
    }

    fun setInputValuesAscii(value: String, channel: Int = 0) {
        setInputValues(mutableListOf<Long>().also { list -> value.chars().forEach { c -> list.add(c.toLong()) } }, channel)
    }

    fun getOutputValues(channel: Int = 0, clearChannel: Boolean = true): List<Long> {
        val outputValues = outputChannels[channel].data
        if (clearChannel)
            outputChannels[channel].data.removeAll { true }
        return outputValues
    }

    fun getOutputValuesAscii(channel: Int = 0, clearChannel: Boolean = true): String {
        val outputValues = getOutputValues(channel, clearChannel)
        return StringBuilder().also { s -> outputValues.forEach { l -> s.append(l.toInt().toChar()) } }.toString()
    }

    //////// read input
    private fun readDirect(inputChannel: IoChannel): Long {
        if (inputChannel.data.isEmpty())
            throw AocException("no more input")
        val result = inputChannel.data.removeAt(0)
        log.info("read direct returns [$result]")
        return result
    }

    private fun readFromPipe(pipeChannel: IoChannel): Long {
        val result: Long
        synchronized(pipeChannel.syncObject) {
            while (!pipeChannel.syncObject.dataReady)
                pipeChannel.syncObject.wait()
            result = pipeChannel.data.removeAt(0)
            if (pipeChannel.data.isEmpty())
                pipeChannel.syncObject.dataReady = false
        }
        log.info("read from pipe returns [$result]")
        return result
    }

    fun readInput(): Long {
        val inputChannelIndex = if (Thread.currentThread().name == "main") 0 else Thread.currentThread().name.last().digitToInt()
        log.info("read input called channel $inputChannelIndex")
        val inputChannel = inputChannels[inputChannelIndex]
        return if (inputChannel.mode == DIRECT)
            readDirect(inputChannel)
        else
            readFromPipe(inputChannel)
    }

    //////// write output
    private fun printDirect(outputChannel: IoChannel, value: Long) {
        if (useStdout)
            print(value.toInt().toChar())
        else {
            log.info("print direct value [$value]")
            outputChannel.data.add(value)
        }
    }

    private fun printToPipe(pipeChannel: IoChannel, value: Long) {
        synchronized(pipeChannel.syncObject) {
            pipeChannel.data.add(value)
            pipeChannel.syncObject.dataReady = true
            pipeChannel.syncObject.notify()
        }
    }

    fun printOutput(value: Long) {
        val outputChannelIndex = if (Thread.currentThread().name == "main") 0 else Thread.currentThread().name.last().digitToInt()
        log.info("print output called channel $outputChannelIndex, value $value")
        val outputChannel = outputChannels[outputChannelIndex]
        if (outputChannel.mode == DIRECT)
            printDirect(outputChannel, value)
        else
            printToPipe(outputChannel, value)
    }
}

class IoChannel(val data: MutableList<Long> = mutableListOf(),
                     val syncObject: SyncObject = SyncObject(),
                     val mode: IoMode = DIRECT)

class SyncObject: Object() {
    @Volatile
    var dataReady: Boolean = false
}

enum class IoMode {
    DIRECT,
    PIPE
}
