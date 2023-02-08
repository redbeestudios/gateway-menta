package com.menta.api.terminals.shared.error.model

import com.menta.api.terminals.domain.Feature

class InvalidArgumentFeatureError(value: String) :
    RuntimeException(
        "Invalid Feature received: $value, valid Feature ${Feature.values().map { it.name }}",
    )
