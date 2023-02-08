package com.menta.bff.devices.login.entities.customer.service

import arrow.core.left
import arrow.core.right
import com.menta.bff.devices.login.entities.customer.aCustomer
import com.menta.bff.devices.login.entities.customer.application.port.out.FindCustomerPortOut
import com.menta.bff.devices.login.entities.customer.application.service.FindCustomerApplicationService
import com.menta.bff.devices.login.shared.other.error.model.ApplicationError.Companion.notFound
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FeatureSpec
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.UUID

class FindCustomerApplicationServiceSpec : FeatureSpec({

    val findPortOut = mockk<FindCustomerPortOut>()

    val findService = FindCustomerApplicationService(
        findCustomer = findPortOut
    )

    beforeEach { clearAllMocks() }

    feature("find customer by id") {
        val id = UUID.randomUUID()

        scenario("customer found") {
            val customer = aCustomer()

            every { findPortOut.findBy(id) } returns customer.right()

            findService.findBy(id) shouldBeRight customer

            verify(exactly = 1) { findPortOut.findBy(id) }
        }
        scenario("customer not found") {
            val error = notFound("customer $id not found")

            every { findPortOut.findBy(id) } returns error.left()

            findService.findBy(id) shouldBeLeft error

            verify(exactly = 1) { findPortOut.findBy(id) }
        }
    }
})
