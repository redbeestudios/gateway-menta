package com.menta.api.users.adapter.out.model

enum class CognitoFieldName(val fieldName: String) {
    EMAIL("email"),
    EMAIL_VERIFIED("email_verified"),
    MERCHANT_ID("given_name"),
    CUSTOMER_ID("family_name");

    companion object {
        const val EMAIL_VERIFIED_VALUE = "true"
    }
}
