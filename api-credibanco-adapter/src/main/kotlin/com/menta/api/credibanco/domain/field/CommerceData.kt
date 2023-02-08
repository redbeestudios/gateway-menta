package com.menta.api.credibanco.domain.field

import com.menta.api.credibanco.domain.Country

data class CommerceData(
    val name: String?,
    val terminalCity: TerminalCity,
    val state: String,
    val country: Country
) {
    data class TerminalCity(
        val code: String,
        val name: String
    )
}
