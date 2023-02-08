package com.menta.api.login

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@ConfigurationPropertiesScan("com.menta.api.login")
@SpringBootApplication
class ApiLoginApplication

fun main(args: Array<String>) {
    runApplication<ApiLoginApplication>(*args)
}
