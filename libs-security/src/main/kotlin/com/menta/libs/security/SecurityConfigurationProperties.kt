package com.menta.libs.security

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConfigurationProperties("libs.security")
@ConstructorBinding
data class SecurityConfigurationProperties(
    val resourceServer: ResourceServer? = null,
    val ignorePaths: List<String> = listOf("/actuator/**"),
    val requesterUser: RequesterUser,
    val ownership: Ownership
) {

    data class ResourceServer(
        val issuers: List<Issuer>? = null
    ) {
        data class Issuer(
            val name: String,
            val uri: String,
            val authoritiesClaimKey: String
        )
    }

    data class RequesterUser(
        val provider: Provider
    ) {
        data class Provider(
            val enabled: Boolean = false
        )
    }

    data class Ownership(
        val enabled: Boolean = false
    )
}
