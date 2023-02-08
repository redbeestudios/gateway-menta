package com.menta.api.merchants

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan("com.menta.api.merchants")
class ApiMerchantsApplication

fun main(args: Array<String>) {
    runApplication<ApiMerchantsApplication>(*args)
}
