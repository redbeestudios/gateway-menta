package com.kiwi.api.payments

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@ConfigurationPropertiesScan("com.menta")
@SpringBootApplication(scanBasePackages = ["com.menta", "com.kiwi"])
class ApiPaymentsApplication

fun main(args: Array<String>) { runApplication<ApiPaymentsApplication>(*args) }
