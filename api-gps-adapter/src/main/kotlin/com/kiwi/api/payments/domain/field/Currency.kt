package com.kiwi.api.payments.domain.field

import com.kiwi.api.payments.adapter.jpos.provider.leftIfNull
import com.kiwi.api.payments.shared.error.model.InvalidCurrency

enum class Currency(val code: String) {
    USD("840"),
    ARS("032"),
    UYU("858");

    companion object {
        private val codes = values().associateBy { it.code }

        fun from(code: String) = codes[code].leftIfNull(InvalidCurrency(code))
    }
}
