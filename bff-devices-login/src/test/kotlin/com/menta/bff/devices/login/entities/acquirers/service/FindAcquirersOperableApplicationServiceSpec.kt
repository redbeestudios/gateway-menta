package com.menta.bff.devices.login.entities.acquirers.service

import arrow.core.left
import arrow.core.right
import com.menta.bff.devices.login.entities.acquirers.aAcquirersOperable
import com.menta.bff.devices.login.entities.acquirers.application.port.out.FindAcquirersOperablePortOut
import com.menta.bff.devices.login.entities.acquirers.application.service.FindAcquirersOperableApplicationService
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

class FindAcquirersOperableApplicationServiceSpec : FeatureSpec({

    val findPortOut = mockk<FindAcquirersOperablePortOut>()

    val findService = FindAcquirersOperableApplicationService(
        findCustomer = findPortOut
    )

    beforeEach { clearAllMocks() }

    feature("find acquirers operable by customer id") {
        val id = UUID.randomUUID()
        val userAuth = aUserAuthResponseWithToken()

        scenario("acquirers operable found") {
            val acquirers = aAcquirersOperable()

            every { findPortOut.findBy(id, userAuth) } returns acquirers.right()

            findService.findBy(id, userAuth) shouldBeRight acquirers

            verify(exactly = 1) { findPortOut.findBy(id, userAuth) }
        }
        scenario("customer not found") {
            val error = notFound("acquirers operable for customer id $id not found")

            every { findPortOut.findBy(id, userAuth) } returns error.left()

            findService.findBy(id, userAuth) shouldBeLeft error

            verify(exactly = 1) { findPortOut.findBy(id, userAuth) }
        }
    }
})
