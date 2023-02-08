package com.menta.apiacquirers

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["com.menta"])
@ConfigurationPropertiesScan("com.menta")
class ApiAcquirersApplication

fun main(args: Array<String>) {
    runApplication<ApiAcquirersApplication>(*args)
}
