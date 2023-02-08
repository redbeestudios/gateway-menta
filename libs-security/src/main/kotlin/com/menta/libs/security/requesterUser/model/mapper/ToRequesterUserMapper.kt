package com.menta.libs.security.requesterUser.model.mapper

import com.menta.libs.security.requesterUser.exception.InvalidRequesterUserException
import com.menta.libs.security.requesterUser.model.RequesterUser
import com.menta.libs.security.requesterUser.model.mapper.RequesterUserField.CUSTOMER_ID
import com.menta.libs.security.requesterUser.model.mapper.RequesterUserField.EMAIL
import com.menta.libs.security.requesterUser.model.mapper.RequesterUserField.MERCHANT_ID
import com.menta.libs.security.requesterUser.provider.UserTypeProvider
import org.springframework.security.oauth2.jwt.Jwt
import java.util.UUID

class ToRequesterUserMapper(
    private val userTypeProvider: UserTypeProvider
) {

    fun from(jwt: Jwt): RequesterUser =
        with(jwt.claims) {
            RequesterUser(
                type = getTypeFrom(getOrThrow(RequesterUserField.ISSUER) as String),
                name = getOrThrow(EMAIL) as String,
                attributes = RequesterUser.Attributes(
                    customerId = (get(CUSTOMER_ID.jwtClaim) as String?)?.asUUID(),
                    merchantId = (get(MERCHANT_ID.jwtClaim) as String?)?.asUUID(),
                    email = getOrThrow(EMAIL) as String
                )
            )
        }

    private fun getTypeFrom(issuer: String) =
        RequesterUser.UserType.valueOf(userTypeProvider.provideFor(issuer)!!.name)

    private fun <V> Map<String, V?>.getOrThrow(requesterUserField: RequesterUserField): V =
        getOrThrow(requesterUserField.jwtClaim, InvalidRequesterUserException(requesterUserField.jwtClaim))
}

private enum class RequesterUserField(val jwtClaim: String) {
    EMAIL("email"),
    ISSUER("iss"),
    CUSTOMER_ID("customer_id"),
    MERCHANT_ID("merchant_id"),
}

private fun <K, V> Map<K, V?>.getOrThrow(key: K, e: Throwable) =
    get(key) ?: throw e

private fun String.asUUID() =
    UUID.fromString(this)
