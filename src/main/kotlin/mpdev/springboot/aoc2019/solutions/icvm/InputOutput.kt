package mpdev.springboot.aoc2019.solutions.icvm

import org.slf4j.Logger
import org.slf4j.LoggerFactory

object InputOutput {

    private val log: Logger = LoggerFactory.getLogger(this::class.java)

    private lateinit var inputChannels: MutableList<IoChannel>

    private lateinit var outputChannels: MutableList<IoChannel>

    private var useStdout = false
    private var useStdin = false

    private var asciiInputProvided = false

    fun initIoChannel(stdout: Boolean = false, stdin: Boolean = false) {
        useStdout = stdout
        useStdin = stdin
        asciiInputProvided = false
        outputChannels = mutableListOf<IoChannel>().also { list -> list.add(IoChannel()) }
        inputChannels = mutableListOf<IoChannel>().also { list -> list.add(IoChannel()) }
        log.debug("initialised io channels 0")
    }

    fun addIoChannel(ioMode: IOMode, loop: Boolean = false): Int {
        inputChannels.add( if (ioMode == IOMode.DIRECT) IoChannel() else outputChannels.last())
        outputChannels.add(IoChannel())
        log.debug("added io channels {}", outputChannels.lastIndex)
        if (loop){
            inputChannels[0] = outputChannels.last()
            log.debug("feedback loop enabled (first input is connected to last output")
        }
        return outputChannels.lastIndex
    }

    fun setInputValues(values: List<Long>, channel: Int = 0) {
        val inputChannel = inputChannels[channel]
        synchronized(inputChannel) {
            inputChannel.data.addAll(values)
            inputChannel.notify()
        }
        log.debug("set input values for channel [{}] to {}", channel, inputChannels[channel].data)
    }

    fun setInputValuesAscii(value: String, channel: Int = 0) {
        setInputValues(
            mutableListOf<Long>().also { list -> value.chars().forEach { c -> list.add(c.toLong()) } },
            channel
        )
        asciiInputProvided = true
    }

    fun getOutputValues(channel: Int = 0, clearChannel: Boolean = true): List<Long> {
        val outputChannel = outputChannels[channel]
        val outputValues: List<Long>
        log.debug("getOutputValues called for channel {}", channel)
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
                outputChannels[channel].data.removeAll { true }
        }
        return outputValues
    }

    fun getOutputValuesAscii(channel: Int = 0, clearChannel: Boolean = true): String {
        val outputValues = getOutputValues(channel, clearChannel)
        return StringBuilder().also { s -> outputValues.forEach { l -> s.append(l.toInt().toChar()) } }.toString()
    }

    //////// read input
    private fun readFromStdin(): Long {
        val inputChannelIndex = if (Thread.currentThread().name == "main") 0 else Thread.currentThread().name.last().digitToInt()
        val inputChannel = inputChannels[inputChannelIndex]
        if (inputChannel.data.isEmpty()) {
            val inputString = "${readln()}\n"
            println(inputString.trim('\n'))
            inputChannel.data.addAll(mutableListOf<Long>().also { list ->
                inputString.chars().forEach { c -> list.add(c.toLong()) }
            })
        }
        val result = inputChannel.data.removeAt(0)
        log.debug("read direct returns [$result]")
        return result
    }

    private fun readFromChannel(): Long {
        val inputChannelIndex = if (Thread.currentThread().name == "main") 0 else Thread.currentThread().name.last().digitToInt()
        log.debug("read input from channel called channel id: {}", inputChannelIndex)
        val inputChannel = inputChannels[inputChannelIndex]
        val result: Long
        synchronized(inputChannel) {
            while (inputChannel.data.isEmpty())
                inputChannel.wait()
            result = inputChannel.data.removeAt(0)
        }
        log.debug("read from pipe returns [$result]")
        if (asciiInputProvided)
            print(result.toInt().toChar())
        return result
    }

    fun readInput(): Long = if (useStdin)
        readFromStdin()
    else
        readFromChannel()

    //////// write output
    private fun printToStdout(value: Long) {
        print(value.toInt().toChar())
    }

    private fun printToChannel(value: Long) {
        val outputChannelIndex =
            if (Thread.currentThread().name == "main") 0 else Thread.currentThread().name.last().digitToInt()
        log.debug("print output to channel called channel id: {}, value to print: {}", outputChannelIndex, value)
        val outputChannel = outputChannels[outputChannelIndex]
        synchronized(outputChannel) {
            outputChannel.data.add(value)
            outputChannel.notify()
            log.debug("print out to channel has notified - output ready: {}", outputChannel.data)
        }
    }

    fun printOutput(value: Long) {
        if (useStdout)
            printToStdout(value)
        else
            printToChannel(value)
    }
}

class IoChannel(val data: MutableList<Long> = mutableListOf()): Object()

enum class IOMode {
    PIPE,
    DIRECT
}