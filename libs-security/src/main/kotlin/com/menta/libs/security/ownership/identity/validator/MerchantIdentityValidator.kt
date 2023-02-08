package com.menta.libs.security.ownership.identity.validator

import com.menta.libs.security.ownership.identity.OwnerIdentity
import com.menta.libs.security.ownership.identity.provider.OwnerIdentityProvider
import com.menta.libs.security.ownership.identity.validator.exception.MissingIdRequesterUserException
import com.menta.libs.security.ownership.identity.validator.exception.OwnerIdentityMismatchException
import com.menta.libs.security.ownership.owner.Owner
import com.menta.libs.security.requesterUser.model.RequesterUser
import com.menta.libs.security.requesterUser.model.RequesterUser.UserType.MERCHANT
import javax.servlet.http.HttpServletRequest

interface MerchantIdentityValidator : EntityIdentityValidator

class DefaultMerchantIdentityValidator(
    private val ownerIdentityProvider: OwnerIdentityProvider
) : MerchantIdentityValidator {
    override fun validate(source: HttpServletRequest, owner: Owner, requesterUser: RequesterUser) {
        requesterUser
            .shouldHaveMerchantId()
            .shouldBe(ownerIdentityProvider.provideFrom(source, owner))
    }

    private fun RequesterUser.shouldHaveMerchantId() =
        also {
            if (attributes.merchantId == null) {
                throw MissingIdRequesterUserException(MERCHANT)
            }
        }

    private fun RequesterUser.shouldBe(ownerIdentity: OwnerIdentity) =
        also {
            if (type != ownerIdentity.type || attributes.merchantId!! != ownerIdentity.id) {
                throw OwnerIdentityMismatchException(MERCHANT)
            }
        }
}
