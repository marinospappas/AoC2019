package mpdev.springboot.aoc2019.solutions.day02

import kotlin.random.Random

class IntCode(var mem: IntArray) {

    private val memBackup = mem.clone()

    fun restoreMem() {
        mem = memBackup.clone()
    }

    fun run() {
        var ip = 0
        while (ip in mem.indices) {
            val instruction = Instruction(ip, mem)
            if (!instruction.execute())
                break
            ip += instruction.ipIncrement
        }
    }

    fun corruptMem() {      // for testing purposes
        for (i in mem.indices)
            mem[i] = Random.nextInt(100, 200)
    }
}