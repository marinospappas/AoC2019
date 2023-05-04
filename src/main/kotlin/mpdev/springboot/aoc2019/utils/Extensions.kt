package mpdev.springboot.aoc2019.utils

import java.lang.StringBuilder
import java.math.BigInteger

fun Int.big() = BigInteger.valueOf(this.toLong())

fun Long.big() = BigInteger.valueOf(this)

fun String.big() = mutableListOf<BigInteger>().also { list -> this.chars().forEach { c -> list.add(BigInteger(c.toString())) } }

fun String.splitRepeatedChars(): List<String> {
    if (isEmpty())
        return emptyList()
    val s = StringBuilder(this)
    var index = 0
    val delimiter = '_'
    var previous = s.first()
    while (index < s.length) {
        if (s[index] != previous)
            s.insert(index++, delimiter)
        previous = s[index]
        ++index
    }
    return s.split(delimiter)
}