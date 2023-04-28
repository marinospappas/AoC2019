package mpdev.springboot.aoc2019.utils

import java.math.BigInteger

fun Int.big() = BigInteger.valueOf(this.toLong())

fun Long.big() = BigInteger.valueOf(this)

fun String.big() = mutableListOf<BigInteger>().also { list -> this.chars().forEach { c -> list.add(BigInteger(c.toString())) } }