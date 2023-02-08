package com.kiwi.api.payments.hexagonal.adapter.port.out.rest.mapper

import com.kiwi.api.payments.hexagonal.adapter.out.rest.mapper.ToAcquirerRequestMapper
import com.kiwi.api.payments.hexagonal.adapter.out.rest.models.AcquirerRequest
import com.kiwi.api.payments.hexagonal.application.aPayment
import com.kiwi.api.payments.hexagonal.application.anAcquirerRequest
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
                    customer = customer
                )
            }
        }
    }
})
