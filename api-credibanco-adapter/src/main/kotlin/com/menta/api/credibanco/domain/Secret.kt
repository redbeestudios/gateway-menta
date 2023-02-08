package com.menta.api.credibanco.domain

import java.util.UUID

data class Secret(
    val acquirer: Acquirer,
    val type: SecretType? = null,
    val id: UUID? = null,
    val algorithm: String,
    val params: Map<String, String?>
)

enum class Acquirer {
    FEENICIA,
    GPS,
    CREDIBANCO
}

enum class SecretType{
    CUSTOMER, MERCHANT, TERMINAL
}
