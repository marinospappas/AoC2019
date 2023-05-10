package mpdev.springboot.aoc2019.solutions.icvm

import mpdev.springboot.aoc2019.utils.AocException

class ICVMMultipleInstances(val intCodeProgramString: String): ICVM(intCodeProgramString) {

    companion object {
        const val DEF_PROG_INSTANCE_PREFIX = "intcd-inst"
    }

    private var instances = mutableListOf<ICProgram>().also { list -> list.add(program) }
    private val ioChannelMap = mutableMapOf(0 to 0)

    fun cloneInstance(ioMode: IOMode, loop: Boolean = false) {
        val curNumOrInstances = instances.size
        instances.add(ICProgram(intCodeProgramString))
        val newChannel = InputOutput.addIoChannel(ioMode, loop)
        ioChannelMap[curNumOrInstances] = newChannel
    }

    fun runInstance(instanceId: Int, threadNamePrefix: String = DEF_PROG_INSTANCE_PREFIX) {
        runIntCodeProgram("$threadNamePrefix-$instanceId", instances[instanceId])
    }

    fun setInstanceInput(data: Int, instanceId: Int) {
        setProgramInputLong(listOf(data.toLong()), ioChannelMap[instanceId]
            ?: throw AocException("ioChannel not found for instance $instanceId"))
    }

    fun setInstanceInput(data: List<Int>, instanceId: Int) {
        setProgramInputLong(data.map { it.toLong() }, ioChannelMap[instanceId]
            ?: throw AocException("ioChannel not found for instance $instanceId"))
    }

    fun getInstanceOutput(instanceId: Int) =
        getIntCodeProgramOutputLong(instances[instanceId]).map { it.toInt() }

    fun instanceIsRunning(instanceId: Int) =
        instances[instanceId].intCodeThread.state != Thread.State.TERMINATED

    fun waitInstance(instanceId: Int) {
        instances[instanceId].intCodeThread.join()
        log.info("IntCode Instance Thread {} completed", instanceId)
        instances[instanceId].outputThread.interrupt()
    }
}
