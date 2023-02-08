package com.kiwi.api.reimbursements

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan("com.kiwi.api.reimbursements")
class ApiReimbursementsApplication

fun main(args: Array<String>) { runApplication<ApiReimbursementsApplication>(*args) }
