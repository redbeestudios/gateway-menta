package com.kiwi.api.reversal.hexagonal.adapter.out.db.mapper

import com.kiwi.api.reversal.hexagonal.adapter.out.db.resolver.CreateDatetimeResolver
import com.kiwi.api.reversal.hexagonal.application.aCreatedAnnulment
import com.kiwi.api.reversal.hexagonal.application.aCreatedPayment
import com.kiwi.api.reversal.hexagonal.application.aCreatedRefund
import com.kiwi.api.reversal.hexagonal.application.aDatetime
import com.kiwi.api.reversal.hexagonal.application.aOperationEntity
import com.kiwi.api.reversal.hexagonal.application.cardMaskedPan
import com.kiwi.api.reversal.hexagonal.application.cardPan
import com.kiwi.api.reversal.hexagonal.domain.operations.OperationType
import com.kiwi.api.reversal.hexagonal.domain.provider.MaskPanProvider
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.equality.shouldBeEqualToComparingFields
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class ToOperationEntityMapperSpec : FeatureSpec({

    val mockCreateDatetimeResolver = mockk<CreateDatetimeResolver>()
    val mockMaskPanProvider = mockk<MaskPanProvider>()
    val operationMapper = ToOperationEntityMapper(mockMaskPanProvider, mockCreateDatetimeResolver)

    beforeEach { clearAllMocks() }

    feature("map payment operation entity") {
        scenario("successful mapping") {
            val aCreatedPayment = aCreatedPayment()
            val operationEntity = aOperationEntity(OperationType.PAYMENT)

            val createDatetime = aDatetime()

            every { mockCreateDatetimeResolver.provide() } returns createDatetime
            every { mockMaskPanProvider.provide(cardPan) } returns cardMaskedPan

            operationMapper.map(aCreatedPayment) shouldBeEqualToComparingFields operationEntity

            verify(exactly = 1) { mockCreateDatetimeResolver.provide() }
            verify(exactly = 1) { mockMaskPanProvider.provide(cardPan) }
        }
    }

    feature("map refund operation entity") {
        scenario("successful mapping") {
            val aCreatedRefund = aCreatedRefund()
            val operationEntity = aOperationEntity(OperationType.REFUND)

            val createDatetime = aDatetime()

            every { mockCreateDatetimeResolver.provide() } returns createDatetime
            every { mockMaskPanProvider.provide(cardPan) } returns cardMaskedPan

            operationMapper.map(aCreatedRefund) shouldBeEqualToComparingFields operationEntity

            verify(exactly = 1) { mockCreateDatetimeResolver.provide() }
            verify(exactly = 1) { mockMaskPanProvider.provide(cardPan) }
        }
    }

    feature("map annulment operation entity") {
        scenario("successful mapping") {
            val aCreatedAnnulment = aCreatedAnnulment()
            val operationEntity = aOperationEntity(OperationType.ANNULMENT)

            val createDatetime = aDatetime()

            every { mockCreateDatetimeResolver.provide() } returns createDatetime
            every { mockMaskPanProvider.provide(cardPan) } returns cardMaskedPan

            operationMapper.map(aCreatedAnnulment) shouldBeEqualToComparingFields operationEntity

            verify(exactly = 1) { mockCreateDatetimeResolver.provide() }
            verify(exactly = 1) { mockMaskPanProvider.provide(cardPan) }
        }
    }
})
