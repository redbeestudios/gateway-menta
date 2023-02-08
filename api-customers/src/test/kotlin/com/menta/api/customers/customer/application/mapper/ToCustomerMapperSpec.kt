package com.menta.api.customers.customer.application.mapper

import com.menta.api.customers.aCustomerCreated
import com.menta.api.customers.aCustomerId
import com.menta.api.customers.aPreCustomer
import com.menta.api.customers.customer.domain.provider.DateProvider
import com.menta.api.customers.customer.domain.provider.IdProvider
import com.menta.api.customers.datetime
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk

class ToCustomerMapperSpec : FeatureSpec({

    lateinit var idProvider: IdProvider
    lateinit var dateProvider: DateProvider

    feature("map create customer") {

        idProvider = mockk()
        dateProvider = mockk()
        val mapper = ToCustomerMapper(idProvider, dateProvider)

        beforeEach { clearAllMocks() }

        scenario("successful mapping") {
            val preCustomer = aPreCustomer
            val customerId = aCustomerId
            val createdAnnulment = aCustomerCreated

            every { idProvider.provide() } returns customerId
            every { dateProvider.provide() } returns datetime

            mapper.map(preCustomer) shouldBe createdAnnulment
        }
    }
})
