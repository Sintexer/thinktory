package com.mibe.thinktory.telegram

import com.mibe.thinktory.telegram.bot.ThinktoryBot
import eu.vendeli.tgbot.TelegramBot
import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.annotation.Bean
import org.springframework.context.event.EventListener
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@SpringBootApplication
class ThinktoryBotApplication {

    @Bean("botExecutorService")
    fun botExecutorService(): ExecutorService = Executors.newSingleThreadExecutor()
}

fun main(args: Array<String>) {
    SpringApplication.run(ThinktoryBotApplication::class.java, *args)
}