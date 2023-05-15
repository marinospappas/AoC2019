package mpdev.springboot.aoc2019.solutions.icvm

class ICVMMultipleInstances(private val intCodeProgramString: String): ICVM(intCodeProgramString) {

    companion object {
        const val DEF_PROG_INSTANCE_PREFIX = "intcd-inst"
    }

    fun cloneInstance(ioMode: IOMode, loop: Boolean = false) {
        val newInstance = Program(intCodeProgramString)
        threadTable.add(newInstance)
        InputOutput.setIoChannels(threadTable.lastIndex, ioMode, loop)
    }

    fun runInstance(instanceId: Int, threadNamePrefix: String = DEF_PROG_INSTANCE_PREFIX) {
        runIntCodeProgram("$threadNamePrefix-$instanceId", threadTable[instanceId])
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
