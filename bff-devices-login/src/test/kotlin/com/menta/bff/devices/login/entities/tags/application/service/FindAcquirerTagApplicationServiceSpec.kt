package com.menta.bff.devices.login.entities.tags.application.service

import arrow.core.left
import arrow.core.right
import com.menta.bff.devices.login.entities.tags.anAcquirerTagsEmv
import com.menta.bff.devices.login.entities.tags.application.port.out.FindAcquirerTagPortOut
import com.menta.bff.devices.login.entities.tags.tagType
import com.menta.bff.devices.login.entities.user.customerId
import com.menta.bff.devices.login.login.aUserAuthResponseWithToken
import com.menta.bff.devices.login.shared.other.error.model.ApplicationError.Companion.clientError
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FeatureSpec
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.web.reactive.function.client.WebClientResponseException

class FindAcquirerTagApplicationServiceSpec : FeatureSpec({
    val findAcquirerTagPortOut = mockk<FindAcquirerTagPortOut>()
    val service = FindAcquirerTagApplicationService(findAcquirerTagPortOut)

    beforeEach { clearAllMocks() }

    feature("find acquirers tags by customerId") {
        val customerId = customerId
        val type = tagType
        val userAuth = aUserAuthResponseWithToken()

        scenario("acquires tags found") {
            val acquirersEmvs = anAcquirerTagsEmv()

            every { findAcquirerTagPortOut.findBy(customerId, type, userAuth) } returns acquirersEmvs.right()

            service.findTagEmvBy(customerId, userAuth) shouldBeRight acquirersEmvs

            verify(exactly = 1) { findAcquirerTagPortOut.findBy(customerId, type, userAuth) }
        }
        scenario("error searching for acquires tags") {
            val error = clientError(WebClientResponseException(500, "a status text", null, null, null))

            every { findAcquirerTagPortOut.findBy(customerId, type, userAuth) } returns error.left()

            service.findTagEmvBy(customerId, userAuth) shouldBeLeft error

            verify(exactly = 1) { findAcquirerTagPortOut.findBy(customerId, type, userAuth) }
        }
    }
})
