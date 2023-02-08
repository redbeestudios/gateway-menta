package com.menta.libs.security.ownership.owner.provider

import com.menta.libs.security.ownership.annotation.EntityOwnershipValidation
import com.menta.libs.security.ownership.owner.Owner

class DefaultOwnerProvider : OwnerProvider {
    override fun provideFrom(validations: List<EntityOwnershipValidation>): List<Owner> =
        validations.map {
            with(it) {
                Owner(
                    type = owner,
                    argumentSource = argumentSource,
                    argumentName = argumentName
                )
            }
        }
}
