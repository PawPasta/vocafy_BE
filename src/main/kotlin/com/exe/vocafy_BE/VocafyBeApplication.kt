package com.exe.vocafy_BE

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableAsync

@SpringBootApplication
@EnableAsync
class VocafyBeApplication

fun main(args: Array<String>) {
	runApplication<VocafyBeApplication>(*args)
}
