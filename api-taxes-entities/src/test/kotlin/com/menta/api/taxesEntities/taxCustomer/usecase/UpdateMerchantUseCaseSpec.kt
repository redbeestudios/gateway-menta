package com.menta.api.taxesEntities.taxCustomer.usecase

import arrow.core.right
import com.menta.api.taxesEntities.TestConstants.Companion.MERCHANT_ID
import com.menta.api.taxesEntities.aPreTaxMerchant
import com.menta.api.taxesEntities.aTaxMerchant
import com.menta.api.taxesEntities.application.port.out.TaxMerchantRepositoryOutPort
import com.menta.api.taxesEntities.application.usecase.UpdateMerchantUseCase
import com.menta.api.taxesEntities.shared.error.model.ApplicationError.Companion.taxMerchantDoesNotExists
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FeatureSpec
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.Optional
import java.util.UUID

class UpdateMerchantUseCaseSpec : FeatureSpec({

    val repository: TaxMerchantRepositoryOutPort = mockk()
    val useCase = UpdateMerchantUseCase(
        merchantRepository = repository
    )

    beforeEach { clearAllMocks() }

    feature("update tax merchant") {

        scenario("Ok") {
            val merchantId = UUID.fromString(MERCHANT_ID)
            val preTaxMerchant = aPreTaxMerchant()
            val taxMerchant = aTaxMerchant()

            every { repository.findBy(merchantId) } returns Optional.of(taxMerchant)
            every { repository.update(taxMerchant) } returns taxMerchant.right()

            useCase.execute(preTaxMerchant, merchantId) shouldBeRight taxMerchant

            verify(exactly = 1) { repository.findBy(merchantId) }
            verify(exactly = 1) { repository.update(taxMerchant) }
        }

        scenario("Error when tax merchant not found") {
            val merchantId = UUID.fromString(MERCHANT_ID)
            val preTaxMerchant = aPreTaxMerchant()
            val taxMerchant = aTaxMerchant()

            every { repository.findBy(merchantId) } returns Optional.empty()
            useCase.execute(preTaxMerchant, merchantId) shouldBeLeft taxMerchantDoesNotExists()

            verify(exactly = 1) { repository.findBy(merchantId) }
            verify(exactly = 0) { repository.update(taxMerchant) }
        }
    }
})
