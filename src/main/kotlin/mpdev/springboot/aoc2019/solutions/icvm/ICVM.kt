package mpdev.springboot.aoc2019.solutions.icvm

open class ICVM(intCodeProgramString: String, threadNamePrefix: String = DEF_PROG_INSTANCE_PREFIX,
                useStdin: Boolean = false, useStdout: Boolean = false): AbstractICVM() {

    private var mainThread: Program

    init {
        // clears the thread table and creates the first instance of the IntCOde program
        if (threadTable.isNotEmpty())
            threadTable.clear()
        threadTable.add(Program(intCodeProgramString))
        mainThread = threadTable[0]
        mainThread.threadName = "$threadNamePrefix-0"
        InputOutput.setIoChannels(stdin = useStdin, stdout = useStdout)
    }

    fun runProgram() {
        runIntCodeProgram(mainThread)
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