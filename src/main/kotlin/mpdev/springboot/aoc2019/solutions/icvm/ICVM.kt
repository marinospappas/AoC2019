package mpdev.springboot.aoc2019.solutions.icvm

open class ICVM(intCodeProgramString: String): AbstractICVM() {

    protected var program: ICProgram

    init {
        program = ICProgram(intCodeProgramString)
        InputOutput.initIoChannel()
    }

    /// public functions

    fun runProgram(threadName: String = DEF_PROG_THREAD) {
        runIntCodeProgram(threadName, program)
    }

    fun setProgramInput(data: Int) {
        setIntCodeProgramInputLong(listOf(data.toLong()), program)
    }

    fun setProgramInput(data: List<Int>) {
        setIntCodeProgramInputLong(data.map { it.toLong() }, program)
    }

    fun setProgramInputLong(data: List<Long>) {
        setIntCodeProgramInputLong(data, program)
    }

    fun getProgramOutput() = getIntCodeProgramOutputLong(program).map { it.toInt() }

    fun getProgramOutputLong() = getIntCodeProgramOutputLong(program)

    fun programIsRunning() = intCodeProgramIsRunning(program)

    fun waitProgram() {
        waitIntCodeProgram(program)
    }

    fun setProgramMemory(address: Int, data: Int) {
        program.setMemory(address, data)
    }

    fun getProgramMemory(address: Int) = getProgramMemoryLong(address).toInt()

    fun getProgramMemoryLong(address: Int) = program.getMemory(address)

    fun setLimitedMemory() {
        program.setLimitedMemory()
    }
}