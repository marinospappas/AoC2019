package mpdev.springboot.aoc2019.solutions.icvm

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class InputOutput {

    private val log: Logger = LoggerFactory.getLogger(this::class.java)

    private val netIo = NetworkIo()

    init {
        netIo.initialiseNetworkIo()
    }

    fun setIoChannels(icvmInstanceId: Int = 0, ioMode: IOMode = IOMode.DIRECT, loop: Boolean = false) {
        if (ioMode == IOMode.NETWORKED)
            netIo.setIoChannels(icvmInstanceId)
        else {
            AbstractICVM.instanceTable[icvmInstanceId].inputChannel =
                if (ioMode != IOMode.PIPE) DirectIo() else AbstractICVM.instanceTable[icvmInstanceId - 1].outputChannel
            AbstractICVM.instanceTable[icvmInstanceId].outputChannel = DirectIo()
            log.debug("initialised io channels for icvm instance {}", icvmInstanceId)
            if (loop) {
                AbstractICVM.instanceTable[0].inputChannel = AbstractICVM.instanceTable.last().outputChannel
                log.debug("loop i/o activated")
            }
        }
    }

    //////// read input
    @OptIn(ExperimentalCoroutinesApi::class)
    private suspend fun readFromStdin(inputChannel: IoChannel): Long {
        val QUIT_CMD = "quit"
        val channel = inputChannel.data
        if (channel.isEmpty) {
            val inputString = "${readln()}\n"
            println(inputString.trim('\n'))
            if (inputString.trim('\n') == QUIT_CMD)
                AbstractICVM.instanceTable[0].quitProgram = true      // quit only works for main instance
            mutableListOf<Long>().also { list ->
                inputString.chars().forEach { c -> list.add(c.toLong()) }
            }.forEach { channel.send(it) }
        }
        val result = channel.receive()
        log.debug("read direct returns [$result]")
        return result
    }

    private suspend fun readFromChannel(inputChannel: IoChannel): Long {
        val result: Long
        if (inputChannel is NetworkChannel)
            result = netIo.readFromChannel(inputChannel)
        else {
            result = inputChannel.data.receive()
            if (inputChannel.asciiCapable)
                print(result.toInt().toChar())
            log.debug("read from channel returns [$result]")
        }
        return result
    }

    suspend fun readInput(ioChannel: IoChannel): Long = if (ioChannel.useStdin)
        readFromStdin(ioChannel)
    else
        readFromChannel(ioChannel)

    //////// write output
    private fun printToStdout(value: Long) {
        print(value.toInt().toChar())
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private suspend fun printToChannel(outputChannel: IoChannel, value: Long) {
        log.debug("printToChannel called")
        if (outputChannel is NetworkChannel)
            netIo.writeToChannel(value, outputChannel)
        else
        outputChannel.data.send(value)
        log.debug("printToChannel completed - channel empty: {}", outputChannel.data.isEmpty)
    }

    suspend fun printOutput(value: Long, ioChannel: IoChannel) {
        log.debug("printOutput called")
        if (ioChannel.asciiCapable && value < 255)
            printToStdout(value)
        else
            printToChannel(ioChannel, value)
    }
}

open class IoChannel(val data: Channel<Long> = Channel(Channel.UNLIMITED),
                     var asciiCapable: Boolean = false,
                     var useStdin: Boolean = false)

class DirectIo: IoChannel()


enum class IOMode {
    PIPE,
    DIRECT,
    NETWORKED
}