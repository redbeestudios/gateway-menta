package com.menta.api.merchants.shared.error.model.exception

import com.menta.api.merchants.domain.State
import com.menta.api.merchants.shared.error.model.ApplicationError
import java.lang.RuntimeException

class ApplicationErrorException(
    val error: ApplicationError
) : RuntimeException()

class InvalidCountryStateException(value: String) :
    RuntimeException(
        "Invalid State received: $value, valid States ${State.values().map { it.name }}",
    )
