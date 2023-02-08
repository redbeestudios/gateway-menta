package com.menta.libs.security.requesterUser.provider

import com.menta.libs.security.requesterUser.model.RequesterUser
import java.util.UUID

fun aRequesterUser() =
    RequesterUser(
        type = RequesterUser.UserType.MERCHANT,
        name = "email@global.com",
        attributes = RequesterUser.Attributes(
            customerId = UUID.fromString("98683344-2dc8-4202-9915-78f62f016862"),
            merchantId = UUID.fromString("21292eb4-dfff-4c2a-b00a-3d99b9e0f4d0"),
            email = "email@global.com"
        )
    )
