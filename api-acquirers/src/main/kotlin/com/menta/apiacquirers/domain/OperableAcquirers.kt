package com.menta.apiacquirers.domain

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties(prefix = "config")
data class OperableAcquirers(
    val acquirers: List<Acquirer>
) {
    data class Acquirer(
        val name: String,
        val country: String
    )
}
