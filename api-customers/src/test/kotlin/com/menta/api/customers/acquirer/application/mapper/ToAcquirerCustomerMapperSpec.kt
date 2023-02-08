package com.menta.api.customers.acquirer.application.mapper

import com.menta.api.customers.aPreAcquirerCustomer
import com.menta.api.customers.anAcquirerCustomer
import com.menta.api.customers.anAcquirerId
import com.menta.api.customers.customer.domain.provider.DateProvider
import com.menta.api.customers.customer.domain.provider.IdProvider
import com.menta.api.customers.datetime
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk

class ToAcquirerCustomerMapperSpec : FeatureSpec({

    lateinit var idProvider: IdProvider
    lateinit var dateProvider: DateProvider

    feature("map create acquirer customer") {

        idProvider = mockk()
        dateProvider = mockk()
        val mapper = ToAcquirerCustomerMapper(idProvider, dateProvider)

        beforeEach { clearAllMocks() }

        scenario("successful mapping") {
            val preAcquirerCustomer = aPreAcquirerCustomer()
            val acquirerCustomer = anAcquirerCustomer()

            val acquirerId = anAcquirerId

            every { idProvider.provide() } returns acquirerId
            every { dateProvider.provide() } returns datetime

            mapper.map(preAcquirerCustomer) shouldBe acquirerCustomer
        }
    }
})
