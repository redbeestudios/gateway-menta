package com.menta.api.users.shared.other.util

import com.amazonaws.services.cognitoidp.model.CodeMismatchException
import com.menta.api.users.shared.other.error.model.ApplicationError.Companion.unauthorizedUser
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe

class ErrorHandlerSpec : FeatureSpec({

    feature("cognito errors") {

        scenario("with other type value") {
            val email = "email@menta.global"
            val ex = CodeMismatchException("invalid code")

            ex.handleCognitoError(email) shouldBe unauthorizedUser(ex)
        }
    }
})
