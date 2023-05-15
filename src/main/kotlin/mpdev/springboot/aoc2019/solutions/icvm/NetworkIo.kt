package mpdev.springboot.aoc2019.solutions.icvm

object NetworkIo {

    val BROADCAST_ADDRESS = 0xFF

    private lateinit var broadcastQueue: MutableList<Packet>

    fun initialiseNetwork() {
        broadcastQueue = mutableListOf()
    }

    fun buildPacketInOutputChannel(outputValue: Long, outputChannel: NetworkChannel) {
        synchronized(outputChannel) {
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
                outputChannel.data.removeAt(outputChannel.data.lastIndex)
            }
        }
    }

    private fun sendPacketToDestination(packet: Packet) {
        if (packet.address == BROADCAST_ADDRESS)
            broadcastQueue.add(packet)
        else
            InputOutput.setInputValues(listOf(packet.valueX, packet.valueY),
                AbstractICVM.threadTable[packet.address].inputChannel)
    }

    fun getBroadcastQueue() = broadcastQueue.toList()
}

class NetworkChannel(val nicData: MutableList<Packet> = mutableListOf()): IoChannel()

class Packet(val address: Int, var valueX: Long = Long.MIN_VALUE, var valueY: Long = Long.MIN_VALUE) {
    fun isComplete() =
        address in 0..49 && valueX > Long.MIN_VALUE && valueY > Long.MIN_VALUE
}