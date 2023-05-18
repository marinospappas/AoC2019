package mpdev.springboot.aoc2019.solutions.icvm

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class NetworkIo {


    companion object {

        private val BROADCAST_ADDRESS = 0xFF

        private var natPacket: Packet? = null

        private var sentToNode0 = mutableListOf<Long>()
        fun getNatPacket() = natPacket

        suspend fun sendNatPacketTo0() {
            sentToNode0.add(natPacket!!.valueY)
            AbstractICVMc.threadTable[0].inputChannel.data.send(natPacket!!.valueX)
            AbstractICVMc.threadTable[0].inputChannel.data.send(natPacket!!.valueY)
        }

        fun sentSameValueTo0TwiceInARow() =
            sentToNode0.size >=2 && sentToNode0.last() == sentToNode0[sentToNode0.lastIndex-1]

        fun getSentToNode0() = sentToNode0
    }

    private val log: Logger = LoggerFactory.getLogger(this::class.java)

    fun initialiseNetworkIo() {
        natPacket = null
        sentToNode0 = mutableListOf()
    }

    fun setIoChannels(icvmThreadId: Int) {
        AbstractICVMc.threadTable[icvmThreadId].inputChannel = NetworkChannel()
        AbstractICVMc.threadTable[icvmThreadId].outputChannel = NetworkChannel()
        log.debug("initialised network io channels for icvm thread {}", icvmThreadId)
    }

    private val firstTimeDelay = 20L       // initial delay is higher to allow time for the other coroutines to launch
    private val normalDelay = 2L
    private  var delayMsec = firstTimeDelay
    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun readFromChannel(networkChannel: NetworkChannel): Long {
        if (networkChannel.data.isEmpty) {
            delay(delayMsec)
            delayMsec = normalDelay     // subsequent delays can be shorter as everything is now up and running
            return -1
        }
        return networkChannel.data.receive()
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
            natPacket = Packet(BROADCAST_ADDRESS, packet.valueX, packet.valueY)
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