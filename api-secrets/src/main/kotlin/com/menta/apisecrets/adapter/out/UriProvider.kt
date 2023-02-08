package com.menta.apisecrets.adapter.out

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.util.UriComponentsBuilder

@Component
class UriProvider(
    @Value("\${externals.host}")
    private val host: String,
    @Value("\${externals.scheme}")
    private val scheme: String,
    @Value("\${externals.entities.terminals.path}")
    private val terminalPath: String,
    @Value("\${externals.entities.customers.path}")
    private val customerPath: String
) {

    fun provideForTerminals(queryParams: Map<String, String>) =
        baseBuilder()
            .path(terminalPath)
            .queryParams(LinkedMultiValueMap(queryParams.map { it.key to listOf(it.value) }.toMap()))
            .build()
            .toUri()

    fun provideForCustomer(id: String) =
        baseBuilder()
            .path("$customerPath/$id")
            .build(id)

    private fun baseBuilder() =
        UriComponentsBuilder.newInstance()
            .scheme(scheme)
            .host(host)
}
