package com.menta.api.credibanco.domain.field

import com.menta.api.credibanco.adapter.jpos.provider.leftIfNull
import com.menta.api.credibanco.shared.error.model.InvalidCurrency

enum class Currency(val code: String) {
    COP("170");

    companion object {
        private val codes = values().associateBy { it.code }

        fun from(code: String) = codes[code].leftIfNull(InvalidCurrency(code))
    }
}
