package com.menta.api.customers.customer.domain.provider

import org.springframework.beans.factory.annotation.Value
import org.springframework.hateoas.server.mvc.BasicLinkBuilder
import org.springframework.stereotype.Component
import org.springframework.web.util.UriComponentsBuilder

@Component
class AppUrlProvider(
    @Value("\${spring.application.path}")
    private val applicationPath: String
) {

    fun provide() = "${provideHost()}$applicationPath"

    companion object {
        fun provideHost() =
            UriComponentsBuilder
                .fromUriString(BasicLinkBuilder.linkToCurrentMapping().toString())
                .scheme("https").build().toUriString()
    }
}