package mpdev.springboot.aoc2019.solutions.icvm

import org.slf4j.Logger
import org.slf4j.LoggerFactory

object InputOutput {

    private val log: Logger = LoggerFactory.getLogger(this::class.java)

    private var useStdout = false
    private var useStdin = false

    private var asciiInputProvided = false

    fun setIoChannels(icvmThreadId: Int = 0, ioMode: IOMode = IOMode.DIRECT, loop: Boolean = false, stdout: Boolean = false, stdin: Boolean = false) {
        useStdout = stdout
        useStdin = stdin
        asciiInputProvided = false
        AbstractICVM.threadTable[icvmThreadId].inputChannel =
            if (ioMode != IOMode.PIPE) DirectIo() else AbstractICVM.threadTable[icvmThreadId-1].outputChannel
        AbstractICVM.threadTable[icvmThreadId].outputChannel =
            if (ioMode == IOMode.NETWORKED) NetworkChannel() else DirectIo()
        log.debug("initialised io channels for icvm thread {}", icvmThreadId)
        if (loop) {
            AbstractICVM.threadTable[0].inputChannel = AbstractICVM.threadTable.last().outputChannel
            log.debug("loop i/o activated")
        }
    }

    fun setInputValues(values: List<Long>, inputChannel: IoChannel = AbstractICVM.threadTable[0].inputChannel) {
        synchronized(inputChannel) {
            inputChannel.data.addAll(values)
            inputChannel.notify()
        }
    }

    fun setInputValuesAscii(value: String, channel: IoChannel = AbstractICVM.threadTable[0].inputChannel) {
        setInputValues(
            mutableListOf<Long>().also { list -> value.chars().forEach { c -> list.add(c.toLong()) } },
            channel
        )
        asciiInputProvided = true
    }

    fun getOutputValues(outputChannel: IoChannel = AbstractICVM.threadTable[0].outputChannel, clearChannel: Boolean = true): List<Long> {
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

    fun getOutputValuesAscii(outputChannel: IoChannel = AbstractICVM.threadTable[0].outputChannel, clearChannel: Boolean = true): String {
        val outputValues = getOutputValues(outputChannel, clearChannel)
        return StringBuilder().also { s -> outputValues.forEach { l -> s.append(l.toInt().toChar()) } }.toString()
    }

    //////// read input
    private fun readFromStdin(): Long {
        val icvmThreadId = if (Thread.currentThread().name == "main") 0 else Thread.currentThread().name.last().digitToInt()
        val inputChannel = AbstractICVM.threadTable[icvmThreadId].inputChannel
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
        val icvmThreadId = if (Thread.currentThread().name == "main") 0 else Thread.currentThread().name.last().digitToInt()
        log.debug("read input from channel called thread id: {}", icvmThreadId)
        val inputChannel = AbstractICVM.threadTable[icvmThreadId].inputChannel
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
        val icvmThreadId =
            if (Thread.currentThread().name == "main") 0 else Thread.currentThread().name.last().digitToInt()
        log.debug("print output to channel called thread id: {}, value to print: {}", icvmThreadId, value)
        val outputChannel = AbstractICVM.threadTable[icvmThreadId].outputChannel
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

open class IoChannel(val data: MutableList<Long> = mutableListOf()): Object()

class DirectIo: IoChannel()

enum class IOMode {
    PIPE,
    DIRECT,
    NETWORKED
}