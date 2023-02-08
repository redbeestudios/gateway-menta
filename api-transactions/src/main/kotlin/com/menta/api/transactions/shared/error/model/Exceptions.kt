package com.menta.api.transactions.shared.error.model

import com.menta.api.transactions.domain.Feature

class InvalidArgumentFeatureError(value: String) :
    RuntimeException(
        "Invalid Feature received: $value, valid Features ${Feature.values().map { it.name }}",
    )
