package com.menta.api.terminals.acquirer.domain

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties("acquirers")
data class Acquirers(
    val operable: List<Acquirer>
)

data class Acquirer(
    val id: String
)
