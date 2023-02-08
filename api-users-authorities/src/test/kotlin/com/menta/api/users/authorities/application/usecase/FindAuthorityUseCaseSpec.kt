package com.menta.api.users.authorities.application.usecase

import arrow.core.right
import com.menta.api.users.authorities.application.port.out.FindUserAuthorityPortOut
import com.menta.api.users.authorities.domain.UserAuthority
import com.menta.api.users.authorities.domain.UserType.MERCHANT
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class FindAuthorityUseCaseSpec : FeatureSpec({

    val portOut = mockk<FindUserAuthorityPortOut>()
    val useCase = FindAuthorityUseCase(
        provider = portOut
    )

    beforeEach { clearAllMocks() }

    feature("find authorities by userType") {
        val userType = MERCHANT
        val userAuthority = UserAuthority(
            type = MERCHANT,
            authorities = mutableListOf("Annulment::Create", "AnnulmentReversal::Create")
        )

        scenario("authorities found") {
            every { portOut.provideBy(userType) } returns userAuthority.right()

            useCase.execute(userType) shouldBe userAuthority.right()

            verify(exactly = 1) { portOut.provideBy(userType) }
        }
    }
})
