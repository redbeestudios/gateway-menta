package com.menta.api.customers.customer.domain

import com.fasterxml.jackson.annotation.JsonCreator
import com.menta.api.customers.shared.error.model.InvalidArgumentCountryError

enum class Country {
    ARG, MEX;

    companion object {
        @JvmStatic
        @JsonCreator
        fun forValue(value: String): Country =
            values().find { it.name == value } ?: throw InvalidArgumentCountryError(value)
    }
}
