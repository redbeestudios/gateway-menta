package com.menta.api.transactions.adapter.`in`.model.mapper

import com.menta.api.transactions.TestConstants.Companion.TRANSACTION_ID
import com.menta.api.transactions.aTransaction
import com.menta.api.transactions.aTransactionResponse
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import java.util.UUID

class ToTransactionResponseMapperSpec : FeatureSpec({

    val mapper = ToTransactionResponseMapper()

    feature("map transaction to response") {

        scenario("successful map") {
            mapper.mapFrom(
                aTransaction(UUID.fromString(TRANSACTION_ID))
            ) shouldBe aTransactionResponse()
        }
    }
})
