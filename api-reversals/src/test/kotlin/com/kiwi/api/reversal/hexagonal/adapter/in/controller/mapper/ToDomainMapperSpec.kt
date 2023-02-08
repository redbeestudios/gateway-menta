package com.kiwi.api.reversal.hexagonal.adapter.`in`.controller.mapper

import com.kiwi.api.reversal.hexagonal.application.aBatchClose
import com.kiwi.api.reversal.hexagonal.application.aBatchCloseRequest
import com.kiwi.api.reversal.hexagonal.application.aCustomer
import com.kiwi.api.reversal.hexagonal.application.aMerchant
import com.kiwi.api.reversal.hexagonal.application.aPayment
import com.kiwi.api.reversal.hexagonal.application.aPaymentRequest
import com.kiwi.api.reversal.hexagonal.application.aReceivedTerminal
import com.kiwi.api.reversal.hexagonal.application.aRefund
import com.kiwi.api.reversal.hexagonal.application.aReimbursementRequest
import com.kiwi.api.reversal.hexagonal.application.aTerminal
import com.kiwi.api.reversal.hexagonal.application.anAnnulment
import com.kiwi.api.reversal.hexagonal.domain.operations.Annulment
import com.kiwi.api.reversal.hexagonal.domain.operations.BatchClose
import com.kiwi.api.reversal.hexagonal.domain.operations.Payment
import com.kiwi.api.reversal.hexagonal.domain.operations.Refund
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks

class ToDomainMapperSpec : FeatureSpec({
    val mapper = ToDomainMapper()

    feature("map payment") {

        beforeEach { clearAllMocks() }

        scenario("successful mapping") {
            val payment = aPayment()
            val paymentRequest = aPaymentRequest()
            val customer = aCustomer()
            val merchant = aMerchant()
            val terminal = aTerminal()
            val receivedTerminal = aReceivedTerminal()

            mapper.mapToPayment(paymentRequest, merchant, customer, receivedTerminal) shouldBe with(payment) {
                Payment(
                    operationId = operationId,
                    acquirerId = acquirerId,
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

    feature("map annulment") {

        beforeEach { clearAllMocks() }

        scenario("successful mapping") {
            val annulment = anAnnulment()
            val annulmentRequest = aReimbursementRequest()
            val customer = aCustomer()
            val merchant = aMerchant()
            val terminal = aTerminal()
            val receivedTerminal = aReceivedTerminal()

            mapper.mapToAnnulment(annulmentRequest, merchant, customer, receivedTerminal) shouldBe with(annulment) {
                Annulment(
                    operationId = operationId,
                    acquirerId = acquirerId,
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

    feature("map refund") {

        beforeEach { clearAllMocks() }

        scenario("successful mapping") {
            val refund = aRefund()
            val refundRequest = aReimbursementRequest()
            val customer = aCustomer()
            val merchant = aMerchant()
            val terminal = aTerminal()
            val receivedTerminal = aReceivedTerminal()

            mapper.mapToRefund(refundRequest, merchant, customer, receivedTerminal) shouldBe with(refund) {
                Refund(
                    operationId = operationId,
                    acquirerId = acquirerId,
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

    feature("map batch close") {

        beforeEach { clearAllMocks() }

        scenario("successful mapping") {
            val batchClose = aBatchClose()
            val batchCloseRequest = aBatchCloseRequest()
            val customer = aCustomer()
            val merchant = aMerchant()
            val terminal = aTerminal()
            val receivedTerminal = aReceivedTerminal()

            mapper.mapToBatchClose(batchCloseRequest, merchant, customer, receivedTerminal) shouldBe with(batchClose) {
                BatchClose(
                    id = id,
                    authorization = authorization,
                    hostMessage = hostMessage,
                    totals = totals,
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
