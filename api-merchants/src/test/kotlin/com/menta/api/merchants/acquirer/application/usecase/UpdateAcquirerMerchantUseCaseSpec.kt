package com.menta.api.merchants.acquirer.application.usecase

import arrow.core.right
import com.menta.api.merchants.acquirer.aPreAcquirerMerchant
import com.menta.api.merchants.acquirer.anAcquirerMerchant
import com.menta.api.merchants.acquirer.application.port.out.AcquirerMerchantRepositoryOutPort
import com.menta.api.merchants.acquirer.datetime
import com.menta.api.merchants.domain.provider.DateProvider
import com.menta.api.merchants.shared.error.model.ApplicationError.Companion.acquirerMerchantDoesNotExists
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FeatureSpec
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.Optional

class UpdateAcquirerMerchantUseCaseSpec : FeatureSpec({

    lateinit var dateProvider: DateProvider

    val repository = mockk<AcquirerMerchantRepositoryOutPort>()

    beforeEach { clearAllMocks() }

    feature("update acquirer merchant") {
        dateProvider = mockk()

        val useCase = UpdateAcquirerMerchantUseCase(repository, dateProvider)

        scenario("with pre acquirer merchant") {
            val preAcquirerMerchant = aPreAcquirerMerchant
            val acquirerMerchant = anAcquirerMerchant

            every { repository.update(acquirerMerchant) } returns acquirerMerchant.right()
            every { dateProvider.provide() } returns datetime

            useCase.execute(preAcquirerMerchant, Optional.of(acquirerMerchant)) shouldBeRight acquirerMerchant

            verify(exactly = 1) { dateProvider.provide() }
            verify(exactly = 1) { repository.update(acquirerMerchant) }
        }

        scenario("with acquirer merchant not found") {
            val preAcquirerMerchant = aPreAcquirerMerchant
            val acquirerMerchant = anAcquirerMerchant

            useCase.execute(preAcquirerMerchant, Optional.empty()) shouldBeLeft acquirerMerchantDoesNotExists()

            verify(exactly = 0) { repository.update(acquirerMerchant) }
        }
    }
})
