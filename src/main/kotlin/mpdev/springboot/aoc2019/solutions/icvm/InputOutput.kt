package mpdev.springboot.aoc2019.solutions.icvm

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class InputOutput {

    companion object {
        val log: Logger = LoggerFactory.getLogger(this::class.java)

        init {
            NetworkIo.initialiseNetworkIo()
        }

        fun setIoChannels(icvmInstanceId: Int = 0, ioMode: IOMode = IOMode.DIRECT, loop: Boolean = false) {
            if (ioMode == IOMode.NETWORKED)
                NetworkIo.setIoChannels(icvmInstanceId)
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
        suspend fun readFromStdin(inputChannel: IoChannel): Long {
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

        suspend fun readFromChannel(inputChannel: IoChannel): Long {
            val result: Long
            if (inputChannel is NetworkChannel)
                result = NetworkIo.readFromChannel(inputChannel)
            else {
                result = inputChannel.data.receive()
                if (inputChannel.asciiCapable)
                    print(result.toInt().toChar())
                log.debug("read from channel returns [$result]")
            }
            return result
        }

        //////// write output
        fun printToStdout(value: Long) {
            print(value.toInt().toChar())
        }

        @OptIn(ExperimentalCoroutinesApi::class)
        suspend fun printToChannel(outputChannel: IoChannel, value: Long) {
            log.debug("printToChannel called")
            if (outputChannel is NetworkChannel)
                NetworkIo.writeToChannel(value, outputChannel)
            else
                outputChannel.data.send(value)
            log.debug("printToChannel completed - channel empty: {}", outputChannel.data.isEmpty)
        }
    }
}

open class IoChannel(val data: Channel<Long> = Channel(Channel.UNLIMITED),
                     var asciiCapable: Boolean = false,
                     var useStdin: Boolean = false) {
    suspend fun readInput(): Long = if (this.useStdin)
        InputOutput.readFromStdin(this)
    else
        InputOutput.readFromChannel(this)

    suspend fun printOutput(value: Long) {
        InputOutput.log.debug("printOutput called")
        if (this.asciiCapable && value < 255)
            InputOutput.printToStdout(value)
        else
            InputOutput.printToChannel(this, value)
    }
}

class DirectIo: IoChannel()


enum class IOMode {
    PIPE,
    DIRECT,
    NETWORKED
}