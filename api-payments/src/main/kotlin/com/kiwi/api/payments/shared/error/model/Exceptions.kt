package com.kiwi.api.payments.shared.error.model

import com.kiwi.api.payments.hexagonal.domain.Feature

class InvalidArgumentFeatureError(value: String) :
    RuntimeException(
        "Invalid Feature received: $value, valid Features ${Feature.values().map { it.name }}",
    )

class ApplicationErrorException(
    val error: ApplicationError
) : RuntimeException()
