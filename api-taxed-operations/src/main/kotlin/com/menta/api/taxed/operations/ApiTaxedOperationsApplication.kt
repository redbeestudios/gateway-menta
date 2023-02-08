package com.menta.api.taxed.operations

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@ConfigurationPropertiesScan("com.menta")
@SpringBootApplication
class ApiTaxedOperationsApplication

fun main(args: Array<String>) { runApplication<ApiTaxedOperationsApplication>(*args) }
