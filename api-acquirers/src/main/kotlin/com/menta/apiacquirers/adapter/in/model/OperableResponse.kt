package com.menta.apiacquirers.adapter.`in`.model

import java.util.UUID

data class OperableResponse(
    val customerId: UUID,
    val acquirers: List<Acquirer>
) {

    data class Acquirer(
        val acquirerId: String,
        val code: String
    )
}
