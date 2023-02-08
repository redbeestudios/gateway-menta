package com.kiwi.api.reimbursements.hexagonal.domain

import com.fasterxml.jackson.annotation.JsonCreator
import com.kiwi.api.reimbursements.shared.error.model.InvalidArgumentFeatureError

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
