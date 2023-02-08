package com.menta.api.merchants.merchant.application.usecase

import arrow.core.left
import arrow.core.right
import com.menta.api.merchants.aMerchantCreated
import com.menta.api.merchants.aPreMerchant
import com.menta.api.merchants.application.port.out.MerchantRepositoryOutPort
import com.menta.api.merchants.application.usecase.CreateMerchantUseCase
import com.menta.api.merchants.datetime
import com.menta.api.merchants.domain.LegalTypeValidator
import com.menta.api.merchants.domain.provider.DateProvider
import com.menta.api.merchants.domain.provider.IdProvider
import com.menta.api.merchants.shared.error.model.ApplicationError
import com.menta.api.merchants.shared.error.model.ApplicationError.Companion.invalidBusinessOwner
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FeatureSpec
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.Optional
import java.util.UUID

class CreateMerchantUseCaseSpec : FeatureSpec({

    val validator = mockk<LegalTypeValidator>()
    val repository = mockk<MerchantRepositoryOutPort>()
    val mockIdProvider = mockk<IdProvider>()
    val mockDateProvider = mockk<DateProvider>()
    val useCase = CreateMerchantUseCase(repository, mockIdProvider, mockDateProvider, validator)

    beforeEach { clearAllMocks() }

    feature("create merchant") {
        scenario("with merchant valid") {
            val preMerchant = aPreMerchant()
            val merchant = aMerchantCreated()

            every { mockIdProvider.provide() } returns UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389b")
            every { mockDateProvider.provide() } returns datetime
            every { repository.create(merchant) } returns merchant
            every { validator.validate(preMerchant) } returns preMerchant.right()

            useCase.execute(preMerchant, Optional.empty()) shouldBeRight merchant

            verify(exactly = 1) { repository.create(merchant) }
            verify(exactly = 1) { mockIdProvider.provide() }
            verify(exactly = 2) { mockDateProvider.provide() }
            verify(exactly = 1) { validator.validate(preMerchant) }
        }

        scenario("with merchant invalid") {
            val preMerchant = aPreMerchant()
            val merchant = aMerchantCreated()

            every { mockIdProvider.provide() } returns UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389b")
            every { repository.create(merchant) } returns merchant
            every { validator.validate(preMerchant) } returns invalidBusinessOwner().left()

            useCase.execute(preMerchant, Optional.empty()) shouldBeLeft invalidBusinessOwner()

            verify(exactly = 0) { repository.create(merchant) }
            verify(exactly = 0) { mockIdProvider.provide() }
            verify(exactly = 1) { validator.validate(preMerchant) }
        }

        scenario("with existing merchant") {
            val preMerchant = aPreMerchant()
            val merchant = aMerchantCreated()

            every { mockIdProvider.provide() } returns UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389b")
            every { repository.create(merchant) } returns merchant
            every { validator.validate(preMerchant) } returns preMerchant.right()

            useCase.execute(preMerchant, Optional.of(merchant)) shouldBeLeft ApplicationError.merchantExists()

            verify(exactly = 0) { repository.create(merchant) }
            verify(exactly = 0) { mockIdProvider.provide() }
            verify(exactly = 0) { validator.validate(preMerchant) }
        }
    }
})
