package com.menta.api.credibanco.domain.field

import com.menta.api.credibanco.adapter.jpos.provider.leftIfNull
import com.menta.api.credibanco.shared.error.model.InvalidProcessCode

enum class ProcessCode(val code: String) {
    CREDIT_PURCHASE("000030"),
    CREDIT_ANNULMENT("200030"),
    DEBIT_PURCHASE("000000"),
    DEBIT_ANNULMENT("200000");

    companion object {
        private val codes = ProcessCode.values().associateBy { it.code }

        fun from(code: String) = codes[code].leftIfNull(InvalidProcessCode(code))
    }
}
