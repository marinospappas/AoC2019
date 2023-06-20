package mpdev.springboot.aoc2019.solutions.icvm

import kotlinx.coroutines.Job
import mpdev.springboot.aoc2019.solutions.icvm.ProgramState.*

class ICVMMultipleInstances(private val intCodeProgramString: String,
                            instanceNamePrefix: String = DEF_PROG_INSTANCE_PREFIX,
                            ioMode: IOMode = IOMode.DIRECT):
    ICVM(intCodeProgramString, instanceNamePrefix, ioMode) {

    fun cloneInstance(ioMode: IOMode, loop: Boolean = false, instanceNamePrefix: String = DEF_PROG_INSTANCE_PREFIX) {
        addInstance(intCodeProgramString, ioMode, loop, instanceNamePrefix)
    }

    fun addInstance(intCodeProgramString: String, ioMode: IOMode, loop: Boolean = false, instanceNamePrefix: String = DEF_PROG_INSTANCE_PREFIX) {
        val newInstance = Program(intCodeProgramString)
        instanceTable.add(newInstance)
        instanceTable.last().instanceName = "$instanceNamePrefix-${instanceTable.lastIndex}"
        InputOutput.setIoChannels(instanceTable.lastIndex, ioMode = ioMode, loop = loop)
        log.info("IntCode instance [{}] configured", instanceTable.lastIndex)
    }

    suspend fun runInstance(instanceId: Int) {
        log.info("IntCode instance [{}] starting", instanceId)
        runIntCodeProgram(instanceTable[instanceId])
    }

    suspend fun setInstanceInput(data: Int, instanceId: Int) {
        setIntCodeProgramInputLong(listOf(data.toLong()), instanceTable[instanceId])
    }

    suspend fun setInstanceInput(data: List<Int>, instanceId: Int) {
        setIntCodeProgramInputLong(data.map { it.toLong() }, instanceTable[instanceId])
    }

    suspend fun getInstanceOutput(instanceId: Int) =
        getIntCodeProgramOutputLong(instanceTable[instanceId]).map { it.toInt() }

    fun instanceIsRunning(instanceId: Int) = instanceTable[instanceId].programState != COMPLETED

    suspend fun waitInstance(instanceId: Int, job: Job) {
        waitIntCodeProgram(job)
        log.info("IntCode Instance [{}] completed", instanceId)
    }
}
