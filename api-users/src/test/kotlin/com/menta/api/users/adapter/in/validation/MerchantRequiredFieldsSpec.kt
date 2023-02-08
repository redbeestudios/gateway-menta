package com.menta.api.users.adapter.`in`.validation

import com.menta.api.users.adapter.`in`.model.CreateUserRequest
import com.menta.api.users.customerId
import com.menta.api.users.domain.UserType.CUSTOMER
import com.menta.api.users.domain.UserType.MERCHANT
import com.menta.api.users.domain.UserType.SUPPORT
import com.menta.api.users.email
import com.menta.api.users.merchantId
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import javax.validation.ConstraintValidatorContext

class MerchantRequiredFieldsSpec : FeatureSpec({

    val context = mockk<ConstraintValidatorContext>()
    val validator = MerchantRequiredFieldsValidator()

    feature("merchant required fields validator") {
        scenario("valid") {
            validator.isValid(
                CreateUserRequest(
                    userType = MERCHANT,
                    attributes = CreateUserRequest.Attributes(
                        email = email,
                        customerId = customerId,
                        merchantId = merchantId
                    )
                ),
                context
            ) shouldBe true
        }
        scenario("with merchantId and not customerId") {
            validator.isValid(
                CreateUserRequest(
                    userType = MERCHANT,
                    attributes = CreateUserRequest.Attributes(
                        email = email,
                        customerId = null,
                        merchantId = merchantId
                    )
                ),
                context
            ) shouldBe false
        }
        scenario("with customerId and not merchantId") {
            validator.isValid(
                CreateUserRequest(
                    userType = MERCHANT,
                    attributes = CreateUserRequest.Attributes(
                        email = email,
                        customerId = customerId,
                        merchantId = null
                    )
                ),
                context
            ) shouldBe false
        }
        scenario("without customerId and merchantId") {
            validator.isValid(
                CreateUserRequest(
                    userType = MERCHANT,
                    attributes = CreateUserRequest.Attributes(
                        email = email,
                        customerId = null,
                        merchantId = null
                    )
                ),
                context
            ) shouldBe false
        }
        scenario("with other non merchant type") {
            listOf(CUSTOMER, SUPPORT).forAll {
                validator.isValid(
                    CreateUserRequest(
                        userType = it,
                        attributes = CreateUserRequest.Attributes(
                            email = email,
                            customerId = null,
                            merchantId = null
                        )
                    ),
                    context
                ) shouldBe true
            }
        }
        scenario("without user") {
            validator.isValid(null, context) shouldBe false
        }
    }
})
