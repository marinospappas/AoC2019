package mpdev.springboot.aoc2019

import lombok.extern.slf4j.Slf4j
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
@Slf4j
class AoC22019Application

fun main(args: Array<String>) {
    runApplication<AoC22019Application>(*args)
}