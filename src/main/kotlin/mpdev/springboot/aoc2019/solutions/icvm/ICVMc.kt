package mpdev.springboot.aoc2019.solutions.icvm

import kotlinx.coroutines.Job

open class ICVMc(intCodeProgramString: String,
                 threadNamePrefix: String = DEF_PROG_INSTANCE_PREFIX,
                 ioMode: IOMode = IOMode.DIRECT,
                 useStdin: Boolean = false, useStdout: Boolean = false,): AbstractICVMc() {

    private var mainThread: Programc

    init {
        // clears the thread table and creates the first instance of the IntCOde program
        if (threadTable.isNotEmpty())
            threadTable.clear()
        threadTable.add(Programc(intCodeProgramString))
        mainThread = threadTable[0]
        mainThread.threadName = "$threadNamePrefix-0"
        mainThread.io.setIoChannels(ioMode = ioMode, stdin = useStdin, stdout = useStdout)
    }

    suspend fun runProgram() {
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
    }

    // these functions have not been implemented in ICVM MultiInstance

    fun setProgramMemory(address: Int, data: Int) {
        mainThread.setMemory(address, data)
        log.info("IntCode Program completed")
    }

    fun getProgramMemory(address: Int) = getProgramMemoryLong(address).toInt()

    fun getProgramMemoryLong(address: Int) = mainThread.getMemory(address)

    fun setLimitedMemory() {
        mainThread.setLimitedMemory()
    }
}