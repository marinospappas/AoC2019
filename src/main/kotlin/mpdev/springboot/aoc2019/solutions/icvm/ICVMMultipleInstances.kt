package mpdev.springboot.aoc2019.solutions.icvm

class ICVMMultipleInstances(private val intCodeProgramString: String,
                            threadNamePrefix: String = DEF_PROG_INSTANCE_PREFIX,
                            ioMode: IOMode = IOMode.DIRECT):
    ICVM(intCodeProgramString, threadNamePrefix, ioMode) {

    fun cloneInstance(ioMode: IOMode, loop: Boolean = false, threadNamePrefix: String = DEF_PROG_INSTANCE_PREFIX) {
        val newInstance = Program(intCodeProgramString)
        threadTable.add(newInstance)
        threadTable.last().threadName = "$threadNamePrefix-${threadTable.lastIndex}"
        InputOutput.setIoChannels(threadTable.lastIndex, ioMode = ioMode, loop = loop)
    }

    fun runInstance(instanceId: Int) {
        runIntCodeProgram(threadTable[instanceId])
    }

    fun setInstanceInput(data: Int, instanceId: Int) {
        setIntCodeProgramInputLong(listOf(data.toLong()), threadTable[instanceId])
    }

    fun setInstanceInput(data: List<Int>, instanceId: Int) {
        setIntCodeProgramInputLong(data.map { it.toLong() }, threadTable[instanceId])
    }

    fun getInstanceOutput(instanceId: Int) =
        getIntCodeProgramOutputLong(threadTable[instanceId]).map { it.toInt() }

    fun instanceIsRunning(instanceId: Int) =
        threadTable[instanceId].intCodeThread.state != Thread.State.TERMINATED

    fun waitInstance(instanceId: Int) {
        threadTable[instanceId].intCodeThread.join()
        log.info("IntCode Instance Thread {} completed", instanceId)
    }
}
