package com.kiwi.api.reimbursements.shared.error.model

import com.kiwi.api.reimbursements.hexagonal.domain.Feature

class InvalidArgumentFeatureError(value: String) :
    RuntimeException(
        "Invalid Feature received: $value, valid Features ${Feature.values().map { it.name }}",
    )
