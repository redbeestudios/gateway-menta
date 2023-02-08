package com.menta.libs.security.requesterUser.provider

import com.menta.libs.security.requesterUser.model.RequesterUser

fun interface RequesterUserProvider {
    fun provide(): RequesterUser
}
