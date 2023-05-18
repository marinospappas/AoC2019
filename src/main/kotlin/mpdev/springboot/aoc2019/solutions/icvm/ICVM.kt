package mpdev.springboot.aoc2019.solutions.icvm

import kotlinx.coroutines.Job

open class ICVM(intCodeProgramString: String,
                threadNamePrefix: String = DEF_PROG_INSTANCE_PREFIX,
                ioMode: IOMode = IOMode.DIRECT,
                useStdin: Boolean = false, useStdout: Boolean = false,): AbstractICVM() {

    private var mainThread: Program

    init {
        // clears the thread table and creates the first instance of the IntCOde program
        if (threadTable.isNotEmpty())
            threadTable.clear()
        threadTable.add(Program(intCodeProgramString))
        mainThread = threadTable[0]
        mainThread.threadName = "$threadNamePrefix-0"
        mainThread.io.setIoChannels(ioMode = ioMode, stdin = useStdin, stdout = useStdout)
        log.info("IntCode instance [0] configured")
    }

    suspend fun runProgram() {
        log.info("IntCode instance [0] starting")
        runIntCodeProgram(mainThread)
    }

    suspend fun setProgramInput(data: Int) {
        setIntCodeProgramInputLong(listOf(data.toLong()), mainThread)
    }

    suspend fun setProgramInput(data: List<Int>) {
        setIntCodeProgramInputLong(data.map { it.toLong() }, mainThread)
    }

    suspend fun getProgramOutput() = getIntCodeProgramOutputLong(mainThread).map { it.toInt() }

    suspend fun getProgramOutputLong() = getIntCodeProgramOutputLong(mainThread)

    fun programIsRunning() = intCodeProgramIsRunning(mainThread)

    suspend fun waitProgram(job: Job) {
        waitIntCodeProgram(job)
        log.info("IntCode instance [0] completed")
    }

    // these functions have not been implemented in ICVM MultiInstance

    fun setProgramMemory(address: Int, data: Int) {
        mainThread.setMemory(address, data)
    }

    fun getProgramMemory(address: Int) = getProgramMemoryLong(address).toInt()

    fun getProgramMemoryLong(address: Int) = mainThread.getMemory(address)

    fun setLimitedMemory() {
        mainThread.setLimitedMemory()
    }
}