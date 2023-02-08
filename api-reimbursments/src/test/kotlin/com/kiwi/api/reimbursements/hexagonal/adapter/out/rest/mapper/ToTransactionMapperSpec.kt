package com.kiwi.api.reimbursements.hexagonal.adapter.out.rest.mapper

import com.kiwi.api.reimbursements.hexagonal.application.aTransaction
import com.kiwi.api.reimbursements.hexagonal.application.aTransactionResponse
import com.kiwi.api.reimbursements.hexagonal.domain.Transaction
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks

class ToTransactionMapperSpec : FeatureSpec({

    feature("map customer") {

        val mapper = ToTransactionMapper()

        beforeEach { clearAllMocks() }

        scenario("successful mapping") {
            val transactionResponse = aTransactionResponse()
            val transaction = aTransaction()

            mapper.map(transactionResponse) shouldBe with(transaction) {
                Transaction(
                    id = id
                )
            }
        }
    }
})
