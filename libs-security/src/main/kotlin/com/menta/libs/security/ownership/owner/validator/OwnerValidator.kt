package com.menta.libs.security.ownership.owner.validator

import com.menta.libs.security.ownership.owner.Owner
import com.menta.libs.security.requesterUser.model.RequesterUser

interface OwnerValidator {
    fun validate(owners: List<Owner>, requesterUser: RequesterUser): Owner
}
