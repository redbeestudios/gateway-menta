package com.kiwi.api.payments.hexagonal.adapter.port.`in`.controller.provider

import com.kiwi.api.payments.hexagonal.adapter.`in`.controller.mapper.ToPaymentMapper
import com.kiwi.api.payments.hexagonal.adapter.`in`.controller.provider.PaymentProvider
import com.kiwi.api.payments.hexagonal.application.aCustomer
import com.kiwi.api.payments.hexagonal.application.aMerchant
import com.kiwi.api.payments.hexagonal.application.aPayment
import com.kiwi.api.payments.hexagonal.application.aPaymentRequest
import com.kiwi.api.payments.hexagonal.application.aReceivedTerminal
import com.kiwi.api.payments.hexagonal.application.port.`in`.FindCustomerPortIn
import com.kiwi.api.payments.hexagonal.application.port.`in`.FindMerchantPortIn
import com.kiwi.api.payments.hexagonal.application.port.`in`.FindTerminalPortIn
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.UUID

class PaymentProviderSpec: FeatureSpec({

    feature("provide payment") {

        lateinit var findCustomer: FindCustomerPortIn
        lateinit var findMerchant: FindMerchantPortIn
        lateinit var findTerminal: FindTerminalPortIn
        lateinit var mapper: ToPaymentMapper

        lateinit var paymentProvider: PaymentProvider

        beforeEach {
            clearAllMocks()

            findCustomer = mockk()
            findMerchant = mockk()
            findTerminal = mockk()
            mapper = mockk()

            paymentProvider = PaymentProvider(
                toPaymentMapper = mapper,
                findCustomerPortIn = findCustomer,
                findMerchantPortIn = findMerchant,
                findTerminalPortIn = findTerminal
            )
        }

        scenario("successful provide") {
            val merchant = aMerchant()
            val customer = aCustomer()
            val terminal = aReceivedTerminal()
            val payment = aPayment()
            val request = aPaymentRequest()
            val entityId = UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389b")

            // given mocked dependencies
            every { findTerminal.execute(entityId) } returns terminal
            every { findCustomer.execute(entityId) } returns customer
            every { findMerchant.execute(entityId) } returns merchant
            every { mapper.map(request,merchant, customer, terminal) } returns payment

            // expect that
            paymentProvider.provide(request) shouldBe payment

            // dependencies called

            verify(exactly = 1) { findTerminal.execute(entityId) }
            verify(exactly = 1) { findCustomer.execute(entityId) }
            verify(exactly = 1) { findMerchant.execute(entityId) }
            verify(exactly = 1) { mapper.map(request,merchant, customer, terminal) }

        }
    }
})
