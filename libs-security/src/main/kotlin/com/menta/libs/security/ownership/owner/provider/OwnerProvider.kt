package com.menta.libs.security.ownership.owner.provider

import com.menta.libs.security.ownership.annotation.EntityOwnershipValidation
import com.menta.libs.security.ownership.owner.Owner

interface OwnerProvider {
    fun provideFrom(validations: List<EntityOwnershipValidation>): List<Owner>
}
