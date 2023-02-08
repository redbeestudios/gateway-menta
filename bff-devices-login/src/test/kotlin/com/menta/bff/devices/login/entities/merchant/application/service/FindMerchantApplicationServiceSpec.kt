package com.menta.bff.devices.login.entities.merchant.application.service

import arrow.core.left
import arrow.core.right
import com.menta.bff.devices.login.entities.merchant.aMerchant
import com.menta.bff.devices.login.entities.merchant.application.out.FindMerchantPortOut
import com.menta.bff.devices.login.shared.other.error.model.ApplicationError.Companion.notFound
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FeatureSpec
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.UUID

class FindMerchantApplicationServiceSpec : FeatureSpec({

    val findPortOut = mockk<FindMerchantPortOut>()

    val findService = FindMerchantApplicationService(
        findMerchant = findPortOut
    )

    beforeEach { clearAllMocks() }

    feature("find merchant by id") {
        val id = UUID.randomUUID()

        scenario("merchant found") {
            val merchant = aMerchant()

            every { findPortOut.findBy(id) } returns merchant.right()

            findService.findBy(id) shouldBeRight merchant

            verify(exactly = 1) { findPortOut.findBy(id) }
        }
        scenario("merchant not found") {
            val error = notFound("merchant $id not found")

            every { findPortOut.findBy(id) } returns error.left()

            findService.findBy(id) shouldBeLeft error

            verify(exactly = 1) { findPortOut.findBy(id) }
        }
    }
})
