package com.menta.bff.devices.login

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@ConfigurationPropertiesScan("com.menta")
@SpringBootApplication
class BffDevicesLoginApplication

fun main(args: Array<String>) {
    runApplication<BffDevicesLoginApplication>(*args)
}
