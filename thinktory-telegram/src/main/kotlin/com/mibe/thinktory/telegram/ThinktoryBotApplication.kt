package com.mibe.thinktory.telegram

import com.mibe.thinktory.service.ServicePackageScanMarker
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
import org.springframework.data.mongodb.MongoDatabaseFactory
import org.springframework.data.mongodb.MongoTransactionManager
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories
import org.springframework.transaction.TransactionManager
import org.springframework.transaction.annotation.EnableTransactionManagement
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@EnableMongoRepositories(basePackageClasses = [ServicePackageScanMarker::class])
@SpringBootApplication(scanBasePackageClasses = [ThinktoryBotApplication::class, ServicePackageScanMarker::class])
class ThinktoryBotApplication {

    @Bean("botExecutorService")
    fun botExecutorService(): ExecutorService = Executors.newSingleThreadExecutor()

    @Bean
    fun transactionManager(dbFactory: MongoDatabaseFactory) = MongoTransactionManager(dbFactory)
}

fun main(args: Array<String>) {
    SpringApplication.run(ThinktoryBotApplication::class.java, *args)
}
