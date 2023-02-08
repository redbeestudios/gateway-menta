package com.kiwi.api.reversal

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@ConfigurationPropertiesScan("com.menta")
@SpringBootApplication(scanBasePackages = ["com.menta", "com.kiwi"])
class ApiReverseApplication

fun main(args: Array<String>) { runApplication<ApiReverseApplication>(*args) }
