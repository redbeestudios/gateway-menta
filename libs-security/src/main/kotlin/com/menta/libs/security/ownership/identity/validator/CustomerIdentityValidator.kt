package com.menta.libs.security.ownership.identity.validator

import com.menta.libs.security.ownership.identity.OwnerIdentity
import com.menta.libs.security.ownership.identity.provider.OwnerIdentityProvider
import com.menta.libs.security.ownership.identity.validator.exception.MissingIdRequesterUserException
import com.menta.libs.security.ownership.identity.validator.exception.OwnerIdentityMismatchException
import com.menta.libs.security.ownership.owner.Owner
import com.menta.libs.security.requesterUser.model.RequesterUser
import com.menta.libs.security.requesterUser.model.RequesterUser.UserType.CUSTOMER
import javax.servlet.http.HttpServletRequest

interface CustomerIdentityValidator : EntityIdentityValidator

class DefaultCustomerIdentityValidator(
    private val ownerIdentityProvider: OwnerIdentityProvider
) : CustomerIdentityValidator {
    override fun validate(source: HttpServletRequest, owner: Owner, requesterUser: RequesterUser) {
        requesterUser
            .shouldHaveCustomerId()
            .shouldBe(ownerIdentityProvider.provideFrom(source, owner))
    }

    private fun RequesterUser.shouldHaveCustomerId() =
        also {
            if (attributes.customerId == null) {
                throw MissingIdRequesterUserException(CUSTOMER)
            }
        }

    private fun RequesterUser.shouldBe(ownerIdentity: OwnerIdentity) =
        also {
            if (type != ownerIdentity.type || attributes.customerId!! != ownerIdentity.id) {
                throw OwnerIdentityMismatchException(CUSTOMER)
            }
        }
}
