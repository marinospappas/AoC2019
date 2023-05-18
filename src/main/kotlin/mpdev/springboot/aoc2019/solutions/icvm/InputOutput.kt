package mpdev.springboot.aoc2019.solutions.icvm

import org.slf4j.Logger
import org.slf4j.LoggerFactory

class InputOutput {

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
            AbstractICVM.threadTable[icvmThreadId].inputChannel =
                if (ioMode != IOMode.PIPE) DirectIo() else AbstractICVM.threadTable[icvmThreadId - 1].outputChannel
            AbstractICVM.threadTable[icvmThreadId].outputChannel = DirectIo()
            log.debug("initialised io channels for icvm thread {}", icvmThreadId)
            if (loop) {
                AbstractICVM.threadTable[0].inputChannel = AbstractICVM.threadTable.last().outputChannel
                log.debug("loop i/o activated")
            }
        }
    }

    //////// read input
    private fun readFromStdin(): Long {
        val icvmThreadId =  Thread.currentThread().name.substringAfterLast('-', "0").toInt()
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

    private fun readFromChannel(inputChannel: IoChannel): Long {
        val result: Long
        //if (inputChannel is NetworkChannel)
        //    result = netIo.readFromChannel(inputChannel)
        //else {
            synchronized(inputChannel) {
                while (inputChannel.data.isEmpty())
                    inputChannel.wait()
                result = inputChannel.data.removeAt(0)
            }
            log.debug("read from channel returns [$result]")
            if (asciiInputProvided)
                print(result.toInt().toChar())
        //}
        return result
    }

    fun readInput(ioChannel: IoChannel): Long = if (useStdin)
        readFromStdin()
    else
        readFromChannel(ioChannel)

    //////// write output
    private fun printToStdout(value: Long) {
        print(value.toInt().toChar())
    }

    private fun printToChannel(outputChannel: IoChannel, value: Long) {
        //if (outputChannel is NetworkChannel)
        //    netIo.writeToChannel(value, outputChannel)
        //else
            synchronized(outputChannel) {
                outputChannel.data.add(value)
                outputChannel.notify()
                log.debug("print out to channel has notified - output ready: {}", outputChannel.data)
            }
    }

    fun printOutput(value: Long, ioChannel: IoChannel) {
        if (useStdout)
            printToStdout(value)
        else
            printToChannel(ioChannel, value)
    }
}

open class IoChannel(val data: MutableList<Long> = mutableListOf()): Object()

class DirectIo: IoChannel()

enum class IOMode {
    PIPE,
    DIRECT,
    NETWORKED
}