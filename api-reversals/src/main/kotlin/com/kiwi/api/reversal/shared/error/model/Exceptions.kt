package com.kiwi.api.reversal.shared.error.model

import com.kiwi.api.reversal.hexagonal.domain.entities.Feature

class InvalidArgumentFeatureError(value: String) :
    RuntimeException(
        "Invalid Feature received: $value, valid Features ${Feature.values().map { it.name }}",
    )
