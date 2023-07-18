package com.mibe.tips

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class TipsApplication

fun main(args: Array<String>) {
	runApplication<TipsApplication>(*args)
}
