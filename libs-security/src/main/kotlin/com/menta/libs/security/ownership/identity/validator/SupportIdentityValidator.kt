package com.menta.libs.security.ownership.identity.validator

import com.menta.libs.security.ownership.owner.Owner
import com.menta.libs.security.requesterUser.model.RequesterUser
import javax.servlet.http.HttpServletRequest

interface SupportIdentityValidator : EntityIdentityValidator

class DefaultSupportIdentityValidator : SupportIdentityValidator {
    override fun validate(source: HttpServletRequest, owner: Owner, requesterUser: RequesterUser) {}
}
