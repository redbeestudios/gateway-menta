package com.menta.libs.security.ownership.identity.provider

import com.menta.libs.security.ownership.identity.OwnerIdentity
import com.menta.libs.security.ownership.owner.Owner
import javax.servlet.http.HttpServletRequest

interface OwnerIdentityProvider {
    fun provideFrom(request: HttpServletRequest, owner: Owner): OwnerIdentity
}
