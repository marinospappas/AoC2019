package mpdev.springboot.aoc2019.solutions.icvm


const val DEF_PROG_INSTANCE_PREFIX = "intcd-inst"

class ICVMMultipleInstances(val intCodeProgramString: String): ICVM(intCodeProgramString) {

    private var instances = mutableListOf<ICProgram>().also { list -> list.add(program) }
    private val ioChannelMap = mutableMapOf(0 to 0)

    fun cloneInstance(ioMode: IOMode) {
        val curNumOrInstances = instances.size
        instances.add(ICProgram(intCodeProgramString))
        val newChannel = InputOutput.addIoChannel(ioMode)
        ioChannelMap[curNumOrInstances] = newChannel
    }

    fun runInstance(instanceId: Int, threadNamePrefix: String = DEF_PROG_INSTANCE_PREFIX) {
        runIntCodeProgram("$threadNamePrefix-$instanceId", instances[instanceId])
    }

}
