package com.menta.libs.security.ownership.owner

import com.menta.libs.security.requesterUser.model.RequesterUser.UserType

data class Owner(
    val type: UserType,
    val argumentSource: EntityOwnershipArgumentSource,
    val argumentName: String
)
