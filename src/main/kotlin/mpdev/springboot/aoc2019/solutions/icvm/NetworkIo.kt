package mpdev.springboot.aoc2019.solutions.icvm

import org.slf4j.Logger
import org.slf4j.LoggerFactory

class NetworkIo {

    private val BROADCAST_ADDRESS = 0xFF

    companion object {

        private lateinit var broadcastQueue: MutableList<Packet>

        fun getBroadcastQueue() = broadcastQueue.toList()
    }

    private val log: Logger = LoggerFactory.getLogger(this::class.java)

    fun initialiseNetworkIo() {
        broadcastQueue = mutableListOf()
    }

    fun setIoChannels(icvmThreadId: Int) {
        AbstractICVMc.threadTable[icvmThreadId].inputChannel = NetworkChannel()
        AbstractICVMc.threadTable[icvmThreadId].outputChannel = NetworkChannel()
        log.debug("initialised network io channels for icvm thread {}", icvmThreadId)
    }

    suspend fun readFromChannel(networkChannel: NetworkChannel): Long {
        var result: Long
        //Thread.sleep(10)
        if (networkChannel.data.isEmpty) {
            return -1
        }
        result = networkChannel.data.receive()
        return result
    }

    suspend fun writeToChannel(outputValue: Long, outputChannel: NetworkChannel) {
        if (outputChannel.nicData.isEmpty()) {
            outputChannel.nicData.add(Packet(address = outputValue.toInt()))
            return
        }
        val lastPacket = outputChannel.nicData.last()
        if (lastPacket.valueX == Long.MIN_VALUE)
            lastPacket.valueX = outputValue
        else if (lastPacket.valueY == Long.MIN_VALUE)
            lastPacket.valueY = outputValue

        if (lastPacket.isComplete()) {
            sendPacketToDestination(lastPacket)
            outputChannel.nicData.removeAt(outputChannel.nicData.lastIndex)
        }
    }

    private suspend fun sendPacketToDestination(packet: Packet) {
        log.info("network write: {}", packet)
        if (packet.address == BROADCAST_ADDRESS)
            broadcastQueue.add(packet)
        else {
            val inputChannel = AbstractICVMc.threadTable[packet.address].inputChannel
            inputChannel.data.send(packet.valueX)
            inputChannel.data.send(packet.valueY)
        }
    }
}

class NetworkChannel(val nicData: MutableList<Packet> = mutableListOf()): IoChannelc()

data class Packet(val address: Int, var valueX: Long = Long.MIN_VALUE, var valueY: Long = Long.MIN_VALUE) {
    fun isComplete() =
        address in 0..255 && valueX > Long.MIN_VALUE && valueY > Long.MIN_VALUE
}