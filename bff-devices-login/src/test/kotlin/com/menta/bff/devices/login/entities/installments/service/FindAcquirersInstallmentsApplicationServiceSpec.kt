package com.menta.bff.devices.login.entities.installments.service

import arrow.core.left
import arrow.core.right
import com.menta.bff.devices.login.entities.installments.aAcquirersInstallments
import com.menta.bff.devices.login.entities.installments.application.port.out.FindAcquirersInstallmentsPortOut
import com.menta.bff.devices.login.entities.installments.application.service.FindAcquirersInstallmentsApplicationService
import com.menta.bff.devices.login.login.aUserAuthResponseWithToken
import com.menta.bff.devices.login.shared.other.error.model.ApplicationError.Companion.notFound
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FeatureSpec
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.UUID

class FindAcquirersInstallmentsApplicationServiceSpec : FeatureSpec({

    val findPortOut = mockk<FindAcquirersInstallmentsPortOut>()

    val findService = FindAcquirersInstallmentsApplicationService(
        findInstallments = findPortOut
    )

    beforeEach { clearAllMocks() }

    feature("find acquirers installments by customer id") {
        val id = UUID.randomUUID()
        val userAuth = aUserAuthResponseWithToken()

        scenario("acquirers installments found") {
            val installments = listOf(aAcquirersInstallments())

            every { findPortOut.findBy(id, userAuth) } returns installments.right()

            findService.findBy(id, userAuth) shouldBeRight installments

            verify(exactly = 1) { findPortOut.findBy(id, userAuth) }
        }
        scenario("acquirers installments not found") {
            val error = notFound("acquirers installments for customer id $id not found")

            every { findPortOut.findBy(id, userAuth) } returns error.left()

            findService.findBy(id, userAuth) shouldBeLeft error

            verify(exactly = 1) { findPortOut.findBy(id, userAuth) }
        }
    }
})
