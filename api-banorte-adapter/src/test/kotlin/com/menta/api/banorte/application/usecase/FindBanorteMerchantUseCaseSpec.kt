package com.menta.api.banorte.application.usecase

import arrow.core.left
import arrow.core.right
import com.menta.api.banorte.application.aMerchant
import com.menta.api.banorte.application.merchantId
import com.menta.api.banorte.application.port.out.BanorteMerchantClientRepository
import com.menta.api.banorte.shared.error.model.ServerError
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FeatureSpec
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class FindBanorteMerchantUseCaseSpec : FeatureSpec({

    val findPortOut = mockk<BanorteMerchantClientRepository>()

    val findMerchant = FindBanorteMerchantUseCase(
        repository = findPortOut
    )

    beforeEach { clearAllMocks() }

    feature("find merchant by id") {
        val id = merchantId
        val merchant = aMerchant()

        scenario("merchant found") {
            every { findPortOut.findBy(id) } returns merchant.right()

            findMerchant.execute(id) shouldBeRight merchant

            verify(exactly = 1) { findPortOut.findBy(id) }
        }
        scenario("merchant search error") {
            val error = ServerError()

            every { findPortOut.findBy(id) } returns error.left()

            findMerchant.execute(id) shouldBeLeft error

            verify(exactly = 1) { findPortOut.findBy(id) }
        }
    }
})
