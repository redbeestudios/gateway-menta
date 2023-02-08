package com.menta.api.users.authorities

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@ConfigurationPropertiesScan("com.menta")
@SpringBootApplication(scanBasePackages = ["com.menta"])
class ApiUsersAuthoritiesApplication

fun main(args: Array<String>) {
    runApplication<ApiUsersAuthoritiesApplication>(*args)
}
