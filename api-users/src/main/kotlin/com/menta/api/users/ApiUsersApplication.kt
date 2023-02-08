package com.menta.api.users

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@ConfigurationPropertiesScan("com.menta")
@SpringBootApplication(scanBasePackages = ["com.menta"])
class ApiUsersApplication

fun main(args: Array<String>) {
    runApplication<ApiUsersApplication>(*args)
}
