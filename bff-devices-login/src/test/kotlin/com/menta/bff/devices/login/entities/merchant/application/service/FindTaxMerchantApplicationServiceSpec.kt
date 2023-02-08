package com.menta.bff.devices.login.entities.merchant.application.service

import arrow.core.left
import arrow.core.right
import com.menta.bff.devices.login.entities.merchant.aTaxMerchant
import com.menta.bff.devices.login.entities.merchant.application.out.FindTaxMerchantPortOut
import com.menta.bff.devices.login.shared.other.error.model.ApplicationError
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FeatureSpec
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.UUID

class FindTaxMerchantApplicationServiceSpec : FeatureSpec({

    val findPortOut = mockk<FindTaxMerchantPortOut>()

    val findService = FindTaxMerchantApplicationService(
        findTaxMerchant = findPortOut
    )

    beforeEach { clearAllMocks() }

    feature("find tax merchant by id") {
        val id = UUID.randomUUID()

        scenario("merchant found") {
            val taxMerchant = aTaxMerchant()

            every { findPortOut.findBy(id) } returns taxMerchant.right()

            findService.findBy(id) shouldBeRight taxMerchant

            verify(exactly = 1) { findPortOut.findBy(id) }
        }
        scenario("merchant not found") {
            val error = ApplicationError.notFound("tax merchant $id not found")

            every { findPortOut.findBy(id) } returns error.left()

            findService.findBy(id) shouldBeLeft error

            verify(exactly = 1) { findPortOut.findBy(id) }
        }
    }
})
