package com.menta.libs.security.requesterUser.provider

import com.menta.libs.security.SecurityConfigurationProperties
import com.menta.libs.security.requesterUser.model.RequesterUser.UserType

class UserTypeProvider(
    private val properties: SecurityConfigurationProperties
) {
    fun provideFor(issuerUri: String): UserType? =
        properties.resourceServer?.issuers
            ?.find { it.uri == issuerUri }?.name
            ?.let { UserType.valueOf(it.uppercase()) }
}
