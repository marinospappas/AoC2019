package mpdev.springboot.aoc2019.solutions.day07

import mpdev.springboot.aoc2019.utils.AocException
import java.math.BigInteger

object InputOutput {

    private var inputChannels = arrayOf<IoChannel>()

    private var outputChannels = arrayOf<IoChannel>()

    fun initInputOutput(numThreads: Int, loop: Boolean = false) {
        if (!loop) {
            inputChannels = Array(numThreads) { i ->
                when (i) {
                    0 -> InChannel()
                    else -> PipeChannel()
                }
            }
            outputChannels = Array(numThreads) { i ->
                when (i) {
                    numThreads-1 -> OutChannel()
                    else -> inputChannels[i+1]
                }
            }
        }
        else {
            outputChannels = Array(numThreads) { i -> PipeChannel() }
            inputChannels = Array(numThreads) { i ->
                when (i) {
                    0 -> outputChannels.last()
                    else -> outputChannels[i-1]
                }
            }
        }
    }

    //////// read input
    private fun readDirect(inputChannel: InChannel): BigInteger {
        if (inputChannel.data.isEmpty())
            throw AocException("no more input")
        return inputChannel.data.removeAt(0)
    }

    private fun readFromPipe(pipeChannel: PipeChannel): BigInteger {
        val result: BigInteger
        synchronized(pipeChannel.syncObject) {
            while (!pipeChannel.syncObject.dataReady)
                pipeChannel.syncObject.wait()
            result = pipeChannel.data.removeAt(0)
            if (pipeChannel.data.isEmpty())
                pipeChannel.syncObject.dataReady = false
        }
        return result
    }

    fun readInput(): BigInteger {
        val inputChannelIndex = Thread.currentThread().name.last().digitToInt()
        return when (val inputChannel = inputChannels[inputChannelIndex]) {
            is InChannel -> readDirect(inputChannel)
            is PipeChannel -> readFromPipe(inputChannel)
            else -> BigInteger("0")
        }
    }

    //////// write output
    private fun printDirect(outputChannel: OutChannel, value: BigInteger) {
        outputChannel.data.add(value)
    }

    private fun printToPipe(pipeChannel: PipeChannel, value: BigInteger) {
        synchronized(pipeChannel.syncObject) {
            pipeChannel.data.add(value)
            pipeChannel.syncObject.dataReady = true
            pipeChannel.syncObject.notify()
        }
    }

    fun printOutput(value: BigInteger) {
        val outputChannelIndex = Thread.currentThread().name.last().digitToInt()
        when (val outputChannel = inputChannels[outputChannelIndex]) {
            is OutChannel -> printDirect(outputChannel, value)
            is PipeChannel -> printToPipe(outputChannel, value)
            else -> {}
        }
    }
}

class InChannel: IoChannel()

class OutChannel: IoChannel()

class PipeChannel(val syncObject: SyncObject = SyncObject()): IoChannel()

class SyncObject(var dataReady: Boolean = false): Object()

open class IoChannel(val data: MutableList<BigInteger> = mutableListOf())