package com.kiwi.api.payments.hexagonal.adapter.port.out.db.mapper

import com.kiwi.api.payments.hexagonal.adapter.out.db.mapper.ToOperationEntityMapper
import com.kiwi.api.payments.hexagonal.adapter.out.db.resolver.CreateDatetimeResolver
import com.kiwi.api.payments.hexagonal.application.aCreatedPaymentWithMaskedPan
import com.kiwi.api.payments.hexagonal.application.aOperationEntity
import com.kiwi.api.payments.hexagonal.application.aOperationEntityWithStatusReversal
import com.kiwi.api.payments.hexagonal.application.cardMaskedPan
import com.kiwi.api.payments.hexagonal.domain.provider.MaskPanProvider
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.equality.shouldBeEqualToComparingFields
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneOffset

class ToOperationEntityMapperSpec : FeatureSpec({

    val mockCreateDatetimeResolver = mockk<CreateDatetimeResolver>()
    val maskPanProvider = mockk<MaskPanProvider>()
    val operationMapper = ToOperationEntityMapper(mockCreateDatetimeResolver, maskPanProvider)

    beforeEach { clearAllMocks() }

    feature("map operation entity") {
        scenario("successful mapping") {
            val createdPaymentMessage = aCreatedPaymentWithMaskedPan()
            val operationEntity = aOperationEntity()
            val createDatetime = OffsetDateTime.of(LocalDateTime.of(2022, 1, 19, 11, 23, 23), ZoneOffset.UTC)

            every { mockCreateDatetimeResolver.provide() } returns createDatetime
            every { maskPanProvider.provide(any()) } returns cardMaskedPan

            operationMapper.map(createdPaymentMessage) shouldBeEqualToComparingFields operationEntity

            verify(exactly = 1) { mockCreateDatetimeResolver.provide() }
            verify(exactly = 1) { maskPanProvider.provide(any()) }
        }
    }

    feature("map operation with status reversal") {
        scenario("successful mapping") {
            val operationEntity = aOperationEntity()
            val operationEntityReversal = aOperationEntityWithStatusReversal()

            operationMapper.map(operationEntity) shouldBeEqualToComparingFields operationEntityReversal
        }
    }
})
