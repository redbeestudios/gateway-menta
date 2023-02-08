package com.menta.api.taxed.operations.domain

import com.fasterxml.jackson.annotation.JsonCreator
import com.menta.api.taxed.operations.shared.error.model.InvalidArgumentFeatureError

enum class Feature {
    MANUAL,
    STRIPE,
    CHIP,
    CONTACTLESS;

    companion object {
        @JvmStatic
        @JsonCreator
        fun forValue(value: String): Feature =
            values().find { it.name == value } ?: throw InvalidArgumentFeatureError(value)
    }
}
