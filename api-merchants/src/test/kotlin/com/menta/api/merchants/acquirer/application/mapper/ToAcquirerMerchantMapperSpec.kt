package com.menta.api.merchants.acquirer.application.mapper

import com.menta.api.merchants.aPreAcquirerMerchant
import com.menta.api.merchants.acquirer.anAcquirerMerchant
import com.menta.api.merchants.acquirer.id
import com.menta.api.merchants.datetime
import com.menta.api.merchants.domain.provider.CodeProvider
import com.menta.api.merchants.domain.provider.DateProvider
import com.menta.api.merchants.domain.provider.IdProvider
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk

class ToAcquirerMerchantMapperSpec : FeatureSpec({

    lateinit var idProvider: IdProvider
    lateinit var codeProvider: CodeProvider
    lateinit var dateProvider: DateProvider

    feature("map create acquirer merchant") {

        idProvider = mockk()
        codeProvider = mockk()
        dateProvider = mockk()
        val mapper = ToAcquirerMerchantMapper(idProvider, dateProvider, codeProvider)

        beforeEach { clearAllMocks() }

        scenario("successful mapping") {
            val preAcquirerMerchant = aPreAcquirerMerchant()
            val acquirerMerchant = anAcquirerMerchant

            every { idProvider.provide() } returns id
            every { dateProvider.provide() } returns datetime
            every { codeProvider.provide(any(), preAcquirerMerchant.code) } returns "23456789"

            mapper.map(preAcquirerMerchant) shouldBe acquirerMerchant
        }
    }
})
