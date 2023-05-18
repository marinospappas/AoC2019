package mpdev.springboot.aoc2019.solutions.icvm

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class InputOutputc {

    private val log: Logger = LoggerFactory.getLogger(this::class.java)

    private var useStdout = false
    private var useStdin = false

    private var asciiInputProvided = false

    private val netIo = NetworkIo()

    init {
        netIo.initialiseNetworkIo()
    }

    fun setIoChannels(icvmThreadId: Int = 0, ioMode: IOMode = IOMode.DIRECT, loop: Boolean = false, stdout: Boolean = false, stdin: Boolean = false) {
        if (ioMode == IOMode.NETWORKED)
            netIo.setIoChannels(icvmThreadId)
        else {
            useStdout = stdout
            useStdin = stdin
            asciiInputProvided = false
            AbstractICVMc.threadTable[icvmThreadId].inputChannel =
                if (ioMode != IOMode.PIPE) DirectIoc() else AbstractICVMc.threadTable[icvmThreadId - 1].outputChannel
            AbstractICVMc.threadTable[icvmThreadId].outputChannel = DirectIoc()
            log.debug("initialised io channels for icvm instance {}", icvmThreadId)
            if (loop) {
                AbstractICVMc.threadTable[0].inputChannel = AbstractICVMc.threadTable.last().outputChannel
                log.debug("loop i/o activated")
            }
        }
    }

    //////// read input
    @OptIn(ExperimentalCoroutinesApi::class)
    private suspend fun readFromStdin(inputChannel: IoChannelc): Long {
        //val icvmThreadId =  Thread.currentThread().name.substringAfterLast('-', "0").toInt()
        //val inputChannel = AbstractICVMc.threadTable[icvmThreadId].inputChannel
        val channel = inputChannel.data
        if (channel.isEmpty) {
            val inputString = "${readln()}\n"
            println(inputString.trim('\n'))
            mutableListOf<Long>().also { list ->
                inputString.chars().forEach { c -> list.add(c.toLong()) }
            }.forEach { channel.send(it) }
        }
        val result = channel.receive()
        log.debug("read direct returns [$result]")
        return result
    }

    private suspend fun readFromChannel(inputChannel: IoChannelc): Long {
        val result: Long
        if (inputChannel is NetworkChannel)
            result = netIo.readFromChannel(inputChannel)
        else {
            result = inputChannel.data.receive()
            log.debug("read from channel returns [$result]")
            if (asciiInputProvided)
                print(result.toInt().toChar())
        }
        return result
    }

    suspend fun readInput(ioChannel: IoChannelc): Long = if (useStdin)
        readFromStdin(ioChannel)
    else
        readFromChannel(ioChannel)

    //////// write output
    private fun printToStdout(value: Long) {
        print(value.toInt().toChar())
    }

    private suspend fun printToChannel(outputChannel: IoChannelc, value: Long) {
        log.debug("printToChannel called")
        if (outputChannel is NetworkChannel)
            netIo.writeToChannel(value, outputChannel)
        else
        outputChannel.data.send(value)
        log.debug("printToChannel completed - channel empty: {}", outputChannel.data.isEmpty)
    }

    suspend fun printOutput(value: Long, ioChannel: IoChannelc) {
        log.debug("printOutput called")
        if (useStdout)
            printToStdout(value)
        else
            printToChannel(ioChannel, value)
    }
}

open class IoChannelc(val data: Channel<Long> = Channel(Channel.UNLIMITED))

class DirectIoc: IoChannelc()


enum class IOMode_ {
    PIPE,
    DIRECT,
    NETWORKED
}