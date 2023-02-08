package com.menta.api.transactions

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@ConfigurationPropertiesScan("com.menta")
@SpringBootApplication(scanBasePackages = ["com.menta"])
class TransactionsApplication

fun main(args: Array<String>) {
    runApplication<TransactionsApplication>(*args)
}
