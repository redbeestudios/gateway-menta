package com.menta.api.merchants.merchant.application.usecase

import arrow.core.right
import com.menta.api.merchants.aMerchantCreated
import com.menta.api.merchants.application.port.out.MerchantRepositoryOutPort
import com.menta.api.merchants.application.usecase.UpdateMerchantUseCase
import com.menta.api.merchants.domain.resolver.MerchantActualizationResolver
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FeatureSpec
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class UpdateMerchantUseCaseSpec : FeatureSpec({

    val repository = mockk<MerchantRepositoryOutPort>()
    val merchantActualization = mockk<MerchantActualizationResolver>()
    val useCase = UpdateMerchantUseCase(
        merchantRepository = repository,
        merchantActualization = merchantActualization
    )

    beforeEach { clearAllMocks() }

    feature("update merchant") {
        scenario("update successfully") {
            val merchant = aMerchantCreated()

            every { repository.update(merchant) } returns merchant.right()
            every { merchantActualization.resolveActualization(merchant) } returns merchant

            useCase.execute(merchant) shouldBeRight merchant

            verify(exactly = 1) { repository.update(merchant) }
            verify(exactly = 1) { merchantActualization.resolveActualization(merchant) }
        }
    }
})
