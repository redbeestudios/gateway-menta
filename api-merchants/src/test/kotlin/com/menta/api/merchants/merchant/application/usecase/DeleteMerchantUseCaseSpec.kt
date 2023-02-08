package com.menta.api.merchants.merchant.application.usecase

import arrow.core.right
import com.menta.api.merchants.aDeletedMerchant
import com.menta.api.merchants.aMerchant
import com.menta.api.merchants.application.port.out.MerchantRepositoryOutPort
import com.menta.api.merchants.application.usecase.DeleteMerchantUseCase
import com.menta.api.merchants.domain.resolver.MerchantDeletionResolver
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FeatureSpec
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class DeleteMerchantUseCaseSpec : FeatureSpec({

    val deletionResolver = mockk<MerchantDeletionResolver>()
    val repository = mockk<MerchantRepositoryOutPort>()
    val useCase = DeleteMerchantUseCase(repository, deletionResolver)

    beforeEach { clearAllMocks() }

    feature("delete merchant") {

        scenario("with merchant NOT deleted") {
            val merchant = aMerchant()
            val deletedMerchant = aDeletedMerchant()

            every { deletionResolver.resolveDeletion(merchant) } returns deletedMerchant
            every { repository.update(deletedMerchant) } returns deletedMerchant.right()

            useCase.execute(merchant) shouldBeRight deletedMerchant

            verify(exactly = 1) { deletionResolver.resolveDeletion(merchant) }
            verify(exactly = 1) { repository.update(deletedMerchant) }
        }
    }
})
