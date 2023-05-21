package mpdev.springboot.aoc2019.solutions.icvm

import kotlinx.coroutines.Job

open class ICVM(intCodeProgramString: String,
                instanceNamePrefix: String = DEF_PROG_INSTANCE_PREFIX,
                ioMode: IOMode = IOMode.DIRECT): AbstractICVM() {

    private var mainInstance: Program

    init {
        // clears the instance table and creates the first instance of the IntCode program
        if (instanceTable.isNotEmpty())
            instanceTable.clear()
        instanceTable.add(Program(intCodeProgramString))
        mainInstance = instanceTable[0]
        mainInstance.instanceName = "$instanceNamePrefix-0"
        mainInstance.io.setIoChannels(ioMode = ioMode)
        log.info("IntCode instance [0] configured")
    }

    suspend fun runProgram() {
        log.info("IntCode instance [0] starting")
        runIntCodeProgram(mainInstance)
    }

    suspend fun setProgramInput(data: Int) {
        setIntCodeProgramInputLong(listOf(data.toLong()), mainInstance)
    }

    suspend fun setProgramInput(data: List<Int>) {
        setIntCodeProgramInputLong(data.map { it.toLong() }, mainInstance)
    }

    suspend fun getProgramOutput() = getIntCodeProgramOutputLong(mainInstance).map { it.toInt() }

    suspend fun getProgramOutputLong() = getIntCodeProgramOutputLong(mainInstance)

    fun programIsRunning() = intCodeProgramIsRunning(mainInstance)

    suspend fun waitProgram(job: Job) {
        waitIntCodeProgram(job)
        log.info("IntCode instance [0] completed")
    }

    // these functions have not been implemented in ICVM MultiInstance

    fun setAsciiCapable() {
        setAsciiCapable(mainInstance)
    }

    fun useStdin() {
        useStdin(mainInstance)
    }

    fun setProgramMemory(address: Int, data: Int) {
        mainInstance.setMemory(address, data)
    }

    fun getProgramMemory(address: Int) = getProgramMemoryLong(address).toInt()

    fun getProgramMemoryLong(address: Int) = mainInstance.getMemory(address)

    fun setLimitedMemory() {
        mainInstance.setLimitedMemory()
    }
}