package com.menta.api.terminals

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan("com.menta.api.terminals")
class ApiTerminalsApplication

fun main(args: Array<String>) {
    runApplication<ApiTerminalsApplication>(*args)
}
