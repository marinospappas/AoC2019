package mpdev.springboot.aoc2019.solutions.icvm

class ICVMMultipleInstances(private val intCodeProgramString: String): ICVM(intCodeProgramString) {

    companion object {
        const val DEF_PROG_INSTANCE_PREFIX = "intcd-inst"
    }

    private var instances = mutableListOf<ICProgram>().also { list -> list.add(program) }

    fun cloneInstance(ioMode: IOMode, loop: Boolean = false) {
        val newInstance = ICProgram(intCodeProgramString)
        instances.add(newInstance)
        val newChannel = InputOutput.addIoChannel(ioMode, loop)
        newInstance.inputChannelId = newChannel
        newInstance.OutputChannelId = newChannel
    }

    fun runInstance(instanceId: Int, threadNamePrefix: String = DEF_PROG_INSTANCE_PREFIX) {
        runIntCodeProgram("$threadNamePrefix-$instanceId", instances[instanceId])
    }

    fun setInstanceInput(data: Int, instanceId: Int) {
        setIntCodeProgramInputLong(listOf(data.toLong()), instances[instanceId])
    }

    fun setInstanceInput(data: List<Int>, instanceId: Int) {
        setIntCodeProgramInputLong(data.map { it.toLong() }, instances[instanceId])
    }

    fun getInstanceOutput(instanceId: Int) =
        getIntCodeProgramOutputLong(instances[instanceId]).map { it.toInt() }

    fun instanceIsRunning(instanceId: Int) =
        instances[instanceId].intCodeThread.state != Thread.State.TERMINATED

    fun waitInstance(instanceId: Int) {
        instances[instanceId].intCodeThread.join()
        log.info("IntCode Instance Thread {} completed", instanceId)
    }
}
