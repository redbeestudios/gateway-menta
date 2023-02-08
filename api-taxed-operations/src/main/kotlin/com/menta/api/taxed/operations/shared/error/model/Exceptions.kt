package com.menta.api.taxed.operations.shared.error.model

import com.menta.api.taxed.operations.domain.Feature

class InvalidArgumentFeatureError(value: String) :
    RuntimeException(
        "Invalid Feature received: $value, valid Features ${Feature.values().map { it.name }}",
    )
