package com.menta.apisecrets.adapter.`in`.controller.model

import java.util.UUID

data class SecretResponse(
    val context: Context,
    val secrets: List<Secret>
) {
    data class Secret(
        val master: String,
        val ksn: String?
    )

    data class Context(
        val terminal: Terminal
    ) {
        data class Terminal(
            val id: UUID,
            val serialCode: String
        )
    }
}
