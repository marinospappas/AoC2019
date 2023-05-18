package mpdev.springboot.aoc2019.solutions.icvm

import kotlinx.coroutines.Job
import mpdev.springboot.aoc2019.solutions.icvm.ProgramState.*

class ICVMMultipleInstances(private val intCodeProgramString: String,
                            threadNamePrefix: String = DEF_PROG_INSTANCE_PREFIX,
                            ioMode: IOMode = IOMode.DIRECT):
    ICVM(intCodeProgramString, threadNamePrefix, ioMode) {

    fun cloneInstance(ioMode: IOMode, loop: Boolean = false, threadNamePrefix: String = DEF_PROG_INSTANCE_PREFIX) {
        addInstance(intCodeProgramString, ioMode, loop, threadNamePrefix)
    }

    fun addInstance(intCodeProgramString: String, ioMode: IOMode, loop: Boolean = false, threadNamePrefix: String = DEF_PROG_INSTANCE_PREFIX) {
        val newInstance = Program(intCodeProgramString)
        threadTable.add(newInstance)
        threadTable.last().threadName = "$threadNamePrefix-${threadTable.lastIndex}"
        threadTable.last().io.setIoChannels(threadTable.lastIndex, ioMode = ioMode, loop = loop)
        log.info("IntCode instance [{}] configured", threadTable.lastIndex)
    }

    suspend fun runInstance(instanceId: Int) {
        log.info("IntCode instance [{}] starting", instanceId)
        runIntCodeProgram(threadTable[instanceId])
    }

    suspend fun setInstanceInput(data: Int, instanceId: Int) {
        setIntCodeProgramInputLong(listOf(data.toLong()), threadTable[instanceId])
    }

    suspend fun setInstanceInput(data: List<Int>, instanceId: Int) {
        setIntCodeProgramInputLong(data.map { it.toLong() }, threadTable[instanceId])
    }

    suspend fun getInstanceOutput(instanceId: Int) =
        getIntCodeProgramOutputLong(threadTable[instanceId]).map { it.toInt() }

    fun instanceIsRunning(instanceId: Int) = threadTable[instanceId].programState != COMPLETED

    suspend fun waitInstance(instanceId: Int, job: Job) {
        waitIntCodeProgram(job)
        log.info("IntCode Instance [{}] completed", instanceId)
    }
}
