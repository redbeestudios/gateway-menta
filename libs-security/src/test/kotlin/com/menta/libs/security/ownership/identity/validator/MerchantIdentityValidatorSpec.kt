package com.menta.libs.security.ownership.identity.validator

import com.menta.libs.security.ownership.identity.OwnerIdentity
import com.menta.libs.security.ownership.identity.extractor.OwnerIdentityNotFoundException
import com.menta.libs.security.ownership.identity.provider.OwnerIdentityProvider
import com.menta.libs.security.ownership.identity.validator.exception.MissingIdRequesterUserException
import com.menta.libs.security.ownership.identity.validator.exception.OwnerIdentityMismatchException
import com.menta.libs.security.ownership.owner.EntityOwnershipArgumentSource.PATH_VARIABLE
import com.menta.libs.security.ownership.owner.Owner
import com.menta.libs.security.requesterUser.model.RequesterUser
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
import java.util.UUID
import javax.servlet.http.HttpServletRequest

class MerchantIdentityValidatorSpec : FeatureSpec({
    val provider = mockk<OwnerIdentityProvider>()
    val validator = DefaultMerchantIdentityValidator(provider)

    beforeEach { clearAllMocks() }

    feature("validate") {

        scenario("requester doesnt have merchant id") {
            val source = mockk<HttpServletRequest>()
            val owner = Owner(MERCHANT, PATH_VARIABLE, "merchantId")
            val requester = RequesterUser(
                CUSTOMER,
                "a name",
                attributes = RequesterUser.Attributes(
                    customerId = UUID.randomUUID(),
                    email = "an email",
                    merchantId = null
                )
            )


            shouldThrow<MissingIdRequesterUserException> { validator.validate(source, owner, requester) }

        }

        scenario("owner identity not found") {
            val source = mockk<HttpServletRequest>()
            val owner = Owner(MERCHANT, PATH_VARIABLE, "merchantId")
            val requester = aRequesterUser().copy(type = MERCHANT)

            every { provider.provideFrom(source, owner) } throws OwnerIdentityNotFoundException(owner)

            shouldThrow<OwnerIdentityNotFoundException> { validator.validate(source, owner, requester) }

            verify(exactly = 1) { provider.provideFrom(source, owner) }

        }

        scenario("owner identity doesnt match") {
            val source = mockk<HttpServletRequest>()
            val owner = Owner(MERCHANT, PATH_VARIABLE, "merchantId")
            val requester = RequesterUser(
                MERCHANT,
                "a name",
                attributes = RequesterUser.Attributes(
                    customerId = null,
                    email = "an email",
                    merchantId = UUID.randomUUID()
                )
            )
            val identity = OwnerIdentity(UUID.randomUUID(), MERCHANT)

            every { provider.provideFrom(source, owner) } returns identity

            shouldThrow<OwnerIdentityMismatchException> { validator.validate(source, owner, requester) }

            verify(exactly = 1) { provider.provideFrom(source, owner) }

        }


        scenario("owner identity matches") {
            val source = mockk<HttpServletRequest>()
            val owner = Owner(MERCHANT, PATH_VARIABLE, "merchantIdd")
            val requester = RequesterUser(
                MERCHANT,
                "a name",
                attributes = RequesterUser.Attributes(
                    customerId = null,
                    email = "an email",
                    merchantId = UUID.randomUUID()
                )
            )
            val identity = OwnerIdentity(requester.attributes.merchantId!!, MERCHANT)

            every { provider.provideFrom(source, owner) } returns identity

            validator.validate(source, owner, requester) shouldBe Unit

            verify(exactly = 1) { provider.provideFrom(source, owner) }

        }

    }
})