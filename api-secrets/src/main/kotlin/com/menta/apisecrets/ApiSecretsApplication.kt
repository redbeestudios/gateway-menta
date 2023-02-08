package com.menta.apisecrets

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan("com.menta.apisecrets")
class ApiSecretsApplication

fun main(args: Array<String>) {
    runApplication<ApiSecretsApplication>(*args)
}
