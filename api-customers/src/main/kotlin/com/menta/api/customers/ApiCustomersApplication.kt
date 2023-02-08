package com.menta.api.customers

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan("com.menta.api.customers")
class ApiCustomersApplication

fun main(args: Array<String>) {
    runApplication<ApiCustomersApplication>(*args)
}
