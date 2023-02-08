package com.menta.api.seeds

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@ConfigurationPropertiesScan("com.menta")
@SpringBootApplication(scanBasePackages = ["com.menta"])
class ApiSeedsApplication

fun main(args: Array<String>) {
    runApplication<ApiSeedsApplication>(*args)
}
