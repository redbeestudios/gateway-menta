package com.menta.libs.security.ownership.owner.validator

import com.menta.libs.security.ownership.owner.Owner
import com.menta.libs.security.requesterUser.model.RequesterUser

class RequesterUserIsOwnerValidator : OwnerValidator {
    override fun validate(owners: List<Owner>, requesterUser: RequesterUser): Owner =
        owners.firstOrNull { it.type == requesterUser.type }
            ?: throw RequesterUserIsNotOwner()
}

class RequesterUserIsNotOwner : RuntimeException("user is not owner of the resource")
