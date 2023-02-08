package com.menta.api.credibanco

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@ConfigurationPropertiesScan("com.menta")
@SpringBootApplication(scanBasePackages = ["com.menta"])
class ApiCredibancoAdapterApplication

fun main(args: Array<String>) {
    runApplication<ApiCredibancoAdapterApplication>(*args)
}
