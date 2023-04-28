package mpdev.springboot.aoc2019.solutions.day05

import mpdev.springboot.aoc2019.utils.AocException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.random.Random

class Program(var mem: IntArray) {

    companion object {
        private val log: Logger = LoggerFactory.getLogger(this::class.java)
    }

    private val memBackup = mem.clone()

    fun restoreMem() {
        mem = memBackup.clone()
    }

    fun run() {
        var ip = 0
        InputOutput.output = mutableListOf()
        while (ip in mem.indices) {
            try {
                val instruction = Instruction(ip, mem)
                when (val retCode = instruction.execute()) {
                    InstructionReturnCode.EXIT -> return
                    InstructionReturnCode.JUMP -> ip = retCode.additionalData
                    else -> ip += instruction.ipIncrement
                }
            }
            catch (e: AocException) {
                log.error("exception thrown: ${e.message}, ip = $ip")
                return
            }
        }
    }

    fun corruptMem() {      // for testing purposes
        for (i in mem.indices)
            mem[i] = Random.nextInt(100, 200)
    }
}