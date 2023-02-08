package com.menta.api.merchants.acquirer.application.usecase

import com.menta.api.merchants.acquirer.aMerchantId
import com.menta.api.merchants.acquirer.anAcquirerId
import com.menta.api.merchants.acquirer.anAcquirerMerchant
import com.menta.api.merchants.acquirer.application.port.out.AcquirerMerchantRepositoryOutPort
import com.menta.api.merchants.shared.error.model.ApplicationError.Companion.acquirerMerchantNotFound
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FeatureSpec
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.Optional

class FindAcquirerMerchantUseCaseSpec : FeatureSpec({

    val repository = mockk<AcquirerMerchantRepositoryOutPort>()
    val useCase = FindAcquirerMerchantUseCase(repository)

    beforeEach { clearAllMocks() }

    feature("find by acquirer merchant id") {

        scenario("merchant found") {

            every { repository.findBy(anAcquirerId, aMerchantId) } returns Optional.of(anAcquirerMerchant)

            useCase.execute(anAcquirerId, aMerchantId) shouldBeRight anAcquirerMerchant

            verify(exactly = 1) { repository.findBy(anAcquirerId, aMerchantId) }
        }

        scenario("merchant NOT found") {

            every { repository.findBy(anAcquirerId, aMerchantId) } returns Optional.empty()

            useCase.execute(anAcquirerId, aMerchantId) shouldBeLeft acquirerMerchantNotFound(anAcquirerId, aMerchantId)

            verify(exactly = 1) { repository.findBy(anAcquirerId, aMerchantId) }
        }
    }
})
