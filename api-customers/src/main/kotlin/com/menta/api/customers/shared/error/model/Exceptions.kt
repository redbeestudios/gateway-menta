package com.menta.api.customers.shared.error.model

import com.menta.api.customers.customer.domain.Country
import com.menta.api.customers.customer.domain.LegalType

class InvalidArgumentCountryError(value: String) :
    RuntimeException(
        "Invalid Country received: $value, valid Country ${Country.values().map { it.name }}",
    )

class InvalidLegalTypeError(value: String) :
    RuntimeException(
        "Invalid LegalType received: $value, valid LegalType ${LegalType.values().map { it.name }}",
    )
