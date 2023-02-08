package com.menta.api.terminals.domain

import com.fasterxml.jackson.annotation.JsonCreator
import com.menta.api.terminals.shared.error.model.InvalidArgumentFeatureError

enum class Feature {
    MANUAL, STRIPE, CHIP, CONTACTLESS;

    companion object {
        @JvmStatic
        @JsonCreator
        fun forValue(value: String): Feature =
            values().find { it.name == value } ?: throw InvalidArgumentFeatureError(value)
    }
}
