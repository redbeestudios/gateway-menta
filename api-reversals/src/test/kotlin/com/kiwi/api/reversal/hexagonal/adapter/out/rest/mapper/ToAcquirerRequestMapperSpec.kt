package com.kiwi.api.reversal.hexagonal.adapter.out.rest.mapper

import com.kiwi.api.reversal.hexagonal.adapter.out.rest.model.AcquirerRequest
import com.kiwi.api.reversal.hexagonal.application.aPayment
import com.kiwi.api.reversal.hexagonal.application.anAcquirerRequest
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks

class ToAcquirerRequestMapperSpec : FeatureSpec({

    feature("map acquirer request") {

        val mapper = ToAcquirerRequestMapper()

        beforeEach { clearAllMocks() }

        scenario("successful mapping") {
            val payment = aPayment()
            val acquirerRequest = anAcquirerRequest()

            mapper.map(payment) shouldBe with(acquirerRequest) {
                AcquirerRequest(
                    capture = capture,
                    amount = amount,
                    installments = installments,
                    trace = trace,
                    ticket = ticket,
                    batch = batch,
                    terminal = terminal,
                    merchant = merchant,
                    datetime = datetime,
                    customer = customer,
                    retrievalReferenceNumber = retrievalReferenceNumber
                )
            }
        }
    }
})
