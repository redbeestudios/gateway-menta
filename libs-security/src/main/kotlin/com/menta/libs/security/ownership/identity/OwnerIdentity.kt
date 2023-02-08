package com.menta.libs.security.ownership.identity

import com.menta.libs.security.requesterUser.model.RequesterUser.UserType
import java.util.UUID

data class OwnerIdentity(
    val id: UUID,
    val type: UserType
)
