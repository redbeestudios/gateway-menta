package com.kiwi.api.reimbursements.hexagonal.adapter.out.db.mapper

import com.kiwi.api.reimbursements.hexagonal.adapter.out.db.resolver.CreateDatetimeResolver
import com.kiwi.api.reimbursements.hexagonal.application.aCreatedAnnulment
import com.kiwi.api.reimbursements.hexagonal.application.aCreatedRefund
import com.kiwi.api.reimbursements.hexagonal.application.aDatetime
import com.kiwi.api.reimbursements.hexagonal.application.aOperation
import com.kiwi.api.reimbursements.hexagonal.application.cardMaskedPan
import com.kiwi.api.reimbursements.hexagonal.application.cardPan
import com.kiwi.api.reimbursements.hexagonal.domain.OperationType
import com.kiwi.api.reimbursements.hexagonal.domain.provider.MaskPanProvider
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.equality.shouldBeEqualToComparingFields
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class ToOperationMapperSpec : FeatureSpec({

    val mockCreateDatetimeResolver = mockk<CreateDatetimeResolver>()
    val mockMaskPanProvider = mockk<MaskPanProvider>()
    val operationMapper = ToOperationMapper(mockMaskPanProvider, mockCreateDatetimeResolver)

    beforeEach { clearAllMocks() }

    feature("map refund operation") {
        scenario("successful mapping") {
            val aCreatedRefund = aCreatedRefund()
            val operationEntity = aOperation(OperationType.REFUND)

            val createDatetime = aDatetime()

            every { mockCreateDatetimeResolver.provide() } returns createDatetime
            every { mockMaskPanProvider.provide(cardPan) } returns cardMaskedPan

            operationMapper.map(aCreatedRefund) shouldBeEqualToComparingFields operationEntity

            verify(exactly = 1) { mockCreateDatetimeResolver.provide() }
            verify(exactly = 1) { mockMaskPanProvider.provide(cardPan) }
        }
    }

    feature("map annulment operation") {
        scenario("successful mapping") {
            val aCreatedAnnulment = aCreatedAnnulment()
            val operationEntity = aOperation(OperationType.ANNULMENT)

            val createDatetime = aDatetime()

            every { mockCreateDatetimeResolver.provide() } returns createDatetime
            every { mockMaskPanProvider.provide(cardPan) } returns cardMaskedPan

            operationMapper.map(aCreatedAnnulment) shouldBeEqualToComparingFields operationEntity

            verify(exactly = 1) { mockCreateDatetimeResolver.provide() }
            verify(exactly = 1) { mockMaskPanProvider.provide(cardPan) }
        }
    }
})
