package com.kiwi.api.payments.hexagonal.adapter.port.`in`.controller.mapper

import com.kiwi.api.payments.hexagonal.adapter.`in`.controller.mapper.ToPaymentMapper
import com.kiwi.api.payments.hexagonal.application.aCustomer
import com.kiwi.api.payments.hexagonal.application.aMerchant
import com.kiwi.api.payments.hexagonal.application.aPayment
import com.kiwi.api.payments.hexagonal.application.aPaymentRequest
import com.kiwi.api.payments.hexagonal.application.aReceivedTerminal
import com.kiwi.api.payments.hexagonal.application.aTerminal
import com.kiwi.api.payments.hexagonal.domain.Payment
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks

class ToPaymentMapperSpec : FeatureSpec({

    feature("map payment") {

        val mapper = ToPaymentMapper()

        beforeEach { clearAllMocks() }

        scenario("successful mapping") {
            val payment = aPayment()
            val paymentRequest = aPaymentRequest()
            val customer = aCustomer()
            val merchant = aMerchant()
            val findTerminal = aReceivedTerminal()
            val terminal = aTerminal()

            mapper.map(paymentRequest, merchant, customer, findTerminal) shouldBe with(payment) {
                Payment(
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
