package com.menta.api.transactions.adapter.out.db.mapper

import com.menta.api.transactions.TestConstants
import com.menta.api.transactions.aTransaction
import com.menta.api.transactions.aTransactionEntity
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import java.util.UUID

class ToTransactionEntityMapperSpec : FeatureSpec({
    feature("map operation") {
        val mapper = ToTransactionEntityMapper()
        beforeEach { clearAllMocks() }

        scenario("successful mapping") {
            val result = mapper.map(aTransaction(UUID.fromString(TestConstants.TRANSACTION_ID)))

            result shouldBe aTransactionEntity()
        }
    }
})
