package com.menta.libs.security.ownership.identity.validator

import com.menta.libs.security.ownership.identity.validator.exception.MissingEntityIdentityValidator
import com.menta.libs.security.ownership.identity.validator.exception.MissingIdRequesterUserException
import com.menta.libs.security.ownership.owner.EntityOwnershipArgumentSource.PATH_VARIABLE
import com.menta.libs.security.ownership.owner.Owner
import com.menta.libs.security.requesterUser.model.RequesterUser.UserType.CUSTOMER
import com.menta.libs.security.requesterUser.model.RequesterUser.UserType.MERCHANT
import com.menta.libs.security.requesterUser.provider.aRequesterUser
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import javax.servlet.http.HttpServletRequest

class EntityIdentityValidatorStrategySpec : FeatureSpec({
    val customerValidator = mockk<CustomerIdentityValidator>()
    val strategy = EntityIdentityValidatorStrategy(mapOf(CUSTOMER to customerValidator))

    beforeEach { clearAllMocks() }

    feature("validate") {

        scenario("validator not found") {
            val source = mockk<HttpServletRequest>()
            val owner = Owner(MERCHANT, PATH_VARIABLE, "merchantId")
            val user = aRequesterUser().copy(type = MERCHANT)

            shouldThrow<MissingEntityIdentityValidator> { strategy.validate(source, owner, user) }
        }

        scenario("validator found, returns error") {
            val source = mockk<HttpServletRequest>()
            val owner = Owner(CUSTOMER, PATH_VARIABLE, "merchantId")
            val user = aRequesterUser().copy(type = CUSTOMER)

            every { customerValidator.validate(source, owner, user) } throws MissingIdRequesterUserException(CUSTOMER)

            shouldThrow<MissingIdRequesterUserException> { strategy.validate(source, owner, user) }

            verify(exactly = 1) { customerValidator.validate(source, owner, user) }
        }

        scenario("validator found") {
            val source = mockk<HttpServletRequest>()
            val owner = Owner(CUSTOMER, PATH_VARIABLE, "merchantId")
            val user = aRequesterUser().copy(type = CUSTOMER)

            every { customerValidator.validate(source, owner, user) } returns Unit

            strategy.validate(source, owner, user) shouldBe Unit

            verify(exactly = 1) { customerValidator.validate(source, owner, user) }
        }

    }
}) {
}