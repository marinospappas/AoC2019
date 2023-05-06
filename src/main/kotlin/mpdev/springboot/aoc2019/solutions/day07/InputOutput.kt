package mpdev.springboot.aoc2019.solutions.day07

import mpdev.springboot.aoc2019.utils.AocException
import mpdev.springboot.aoc2019.solutions.day07.IoMode.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.math.BigInteger

object InputOutput {

    private val log: Logger = LoggerFactory.getLogger(this::class.java)

    private var inputChannels = arrayOf<IoChannel>()

    private var outputChannels = arrayOf<IoChannel>()

    fun initInputOutput(numThreads: Int, loop: Boolean = false) {
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

    fun setInputValues(channel: Int, values: List<BigInteger>) {
        inputChannels[channel].data.addAll(values)
        inputChannels[channel].syncObject.dataReady = true
        log.info("set input values for channel [$channel] to ${inputChannels[channel].data}")
    }

    fun getOutputValues(channel: Int): List<BigInteger> = outputChannels[channel].data

    //////// read input
    private fun readDirect(inputChannel: IoChannel): BigInteger {
        if (inputChannel.data.isEmpty())
            throw AocException("no more input")
        val result = inputChannel.data.removeAt(0)
        log.info("read direct returns [$result]")
        return result
    }

    private fun readFromPipe(pipeChannel: IoChannel): BigInteger {
        val result: BigInteger
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

    fun readInput(): BigInteger {
        val inputChannelIndex = Thread.currentThread().name.last().digitToInt()
        log.info("read input called channel $inputChannelIndex")
        val inputChannel = inputChannels[inputChannelIndex]
        return if (inputChannel.mode == DIRECT)
            readDirect(inputChannel)
        else
            readFromPipe(inputChannel)
    }

    //////// write output
    private fun printDirect(outputChannel: IoChannel, value: BigInteger) {
        log.info("print direct value [$value]")
        outputChannel.data.add(value)
    }

    private fun printToPipe(pipeChannel: IoChannel, value: BigInteger) {
        synchronized(pipeChannel.syncObject) {
            pipeChannel.data.add(value)
            pipeChannel.syncObject.dataReady = true
            pipeChannel.syncObject.notify()
        }
    }

    fun printOutput(value: BigInteger) {
        val outputChannelIndex = Thread.currentThread().name.last().digitToInt()
        log.info("print output called channel $outputChannelIndex, value $value")
        val outputChannel = outputChannels[outputChannelIndex]
        if (outputChannel.mode == DIRECT)
            printDirect(outputChannel, value)
        else
            printToPipe(outputChannel, value)
    }
}

class IoChannel(val data: MutableList<BigInteger> = mutableListOf(),
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