package com.menta.api.login.shared.domain

import com.fasterxml.jackson.annotation.JsonCreator

enum class UserType {
    MERCHANT, CUSTOMER, SUPPORT;

    companion object {
        @JsonCreator
        @JvmStatic
        fun forValue(value: String): UserType =
            values().find { it.name == value } ?: throw InvalidUserTypeError(value)
    }
}

class InvalidUserTypeError(value: String) : RuntimeException(
    "Invalid user_type received: $value, valid user_type: ${UserType.values().map { it.name }}",
)
