package com.menta.bff.devices.login.login.challenge.adapter.out.model.mapper

import com.menta.bff.devices.login.login.challenge.adapter.out.model.NewPasswordChallengeClientRequest
import com.menta.bff.devices.login.shared.domain.NewPasswordChallengeSolution
import com.menta.bff.devices.login.shared.domain.UserType.CUSTOMER
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks

class ToNewPasswordChallengeClientRequestMapperSpec : FeatureSpec({

    val mapper = ToNewPasswordChallengeClientRequestMapper()

    beforeEach { clearAllMocks() }

    feature("from NewPasswordChallengeSolution") {
        scenario("map") {
            val from = NewPasswordChallengeSolution(
                session = "a session",
                user = "user@user.com",
                userType = CUSTOMER,
                newPassword = "a new password"
            )

            mapper.mapFrom(from) shouldBe NewPasswordChallengeClientRequest(
                session = "a session",
                user = "user@user.com",
                userType = CUSTOMER,
                newPassword = "a new password"
            )
        }
    }
})
