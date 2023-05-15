package mpdev.springboot.aoc2019.solutions.icvm

open class ICVM(intCodeProgramString: String): AbstractICVM() {

    private var mainThread: Program

    init {
        if (threadTable.isNotEmpty())
            threadTable.clear()
        threadTable.add(Program(intCodeProgramString))
        mainThread = threadTable[0]
        InputOutput.setIoChannels()
    }

    fun runProgram(threadName: String = DEF_PROG_THREAD) {
        runIntCodeProgram(threadName, mainThread)
    }

    fun setProgramInput(data: Int) {
        setIntCodeProgramInputLong(listOf(data.toLong()), mainThread)
    }

    fun setProgramInput(data: List<Int>) {
        setIntCodeProgramInputLong(data.map { it.toLong() }, mainThread)
    }

    fun getProgramOutput() = getIntCodeProgramOutputLong(mainThread).map { it.toInt() }

    fun getProgramOutputLong() = getIntCodeProgramOutputLong(mainThread)

    fun programIsRunning() = intCodeProgramIsRunning(mainThread)

    fun waitProgram() {
        waitIntCodeProgram(mainThread)
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