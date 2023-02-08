package com.menta.api.transactions.adapter.out.db.mapper

import com.menta.api.transactions.TestConstants.Companion.TRANSACTION_ID
import com.menta.api.transactions.aOperationEntity
import com.menta.api.transactions.aTransaction
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import java.util.UUID

class ToOperationEntityMapperSpec : FeatureSpec({
    feature("map operation") {
        val transactionMapper = ToTransactionEntityMapper()
        val mapper = ToOperationEntityMapper(transactionMapper)
        val result = mapper.map(aTransaction(UUID.fromString(TRANSACTION_ID)))
        scenario("successful mapping") {
            result shouldBe aOperationEntity()
        }
    }
})
