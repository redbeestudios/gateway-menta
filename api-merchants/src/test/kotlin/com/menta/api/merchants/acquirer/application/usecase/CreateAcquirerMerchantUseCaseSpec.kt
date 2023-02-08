package com.menta.api.merchants.acquirer.application.usecase

import com.menta.api.merchants.aPreAcquirerMerchant
import com.menta.api.merchants.acquirer.anAcquirerMerchant
import com.menta.api.merchants.acquirer.application.mapper.ToAcquirerMerchantMapper
import com.menta.api.merchants.acquirer.application.port.out.AcquirerMerchantRepositoryOutPort
import com.menta.api.merchants.shared.error.model.ApplicationError
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FeatureSpec
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.Optional

class CreateAcquirerMerchantUseCaseSpec : FeatureSpec({

    val repository = mockk<AcquirerMerchantRepositoryOutPort>()
    val toAcquirerMerchantMapper = mockk<ToAcquirerMerchantMapper>()
    val useCase = CreateAcquirerMerchantUseCase(repository, toAcquirerMerchantMapper)

    beforeEach { clearAllMocks() }

    feature("create acquirer merchant") {

        scenario("with pre acquirer merchant") {
            val preAcquirerMerchant = aPreAcquirerMerchant()
            val acquirerMerchant = anAcquirerMerchant

            every { toAcquirerMerchantMapper.map(preAcquirerMerchant) } returns acquirerMerchant
            every { repository.create(acquirerMerchant) } returns acquirerMerchant

            useCase.execute(preAcquirerMerchant, Optional.empty()) shouldBeRight acquirerMerchant

            verify(exactly = 1) { toAcquirerMerchantMapper.map(preAcquirerMerchant) }
            verify(exactly = 1) { repository.create(acquirerMerchant) }
        }

        scenario("with existing merchant") {
            val preAcquirerMerchant = aPreAcquirerMerchant()
            val acquirerMerchant = anAcquirerMerchant

            every { toAcquirerMerchantMapper.map(preAcquirerMerchant) } returns acquirerMerchant
            every { repository.create(acquirerMerchant) } returns acquirerMerchant

            useCase.execute(
                preAcquirerMerchant,
                Optional.of(acquirerMerchant)
            ) shouldBeLeft ApplicationError.acquirerMerchantExists()

            verify(exactly = 0) { toAcquirerMerchantMapper.map(preAcquirerMerchant) }
            verify(exactly = 0) { repository.create(acquirerMerchant) }
        }
    }
})
