package com.kiwi.api.payments.domain.field

import arrow.core.Either
import com.kiwi.api.payments.adapter.jpos.provider.leftIfNull
import com.kiwi.api.payments.shared.error.model.ApplicationError
import com.kiwi.api.payments.shared.error.model.InvalidMTI

enum class MTI(val code: String) {
    ONLINE_OPERATION_REQUEST("0200"),
    ONLINE_OPERATION_RESPONSE("0210"),
    REVERSE_REQUEST("0400"),
    REVERSE_RESPONSE("0410"),
    PING("0800");

    companion object {
        private val codes: Map<String, MTI> = values().associateBy { it.code }

        fun from(code: String): Either<ApplicationError, MTI> = codes[code].leftIfNull(InvalidMTI(code))
    }
}
