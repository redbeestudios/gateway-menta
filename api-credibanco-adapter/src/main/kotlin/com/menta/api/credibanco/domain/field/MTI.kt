package com.menta.api.credibanco.domain.field

import arrow.core.Either
import com.menta.api.credibanco.adapter.jpos.provider.leftIfNull
import com.menta.api.credibanco.shared.error.model.ApplicationError
import com.menta.api.credibanco.shared.error.model.InvalidMTI

enum class MTI(val code: String) {
    ONLINE_OPERATION_REQUEST("0200"),
    ONLINE_OPERATION_RESPONSE("0210"),
    REVERSE_REQUEST("0420"),
    REVERSE_RESPONSE("0430"),
    PING("0800");

    companion object {
        private val codes: Map<String, MTI> = values().associateBy { it.code }

        fun from(code: String): Either<ApplicationError, MTI> = codes[code].leftIfNull(InvalidMTI(code))
    }
}
