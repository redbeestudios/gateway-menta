package com.menta.libs.security.ownership.identity.provider

import com.menta.libs.security.ownership.identity.OwnerIdentity
import com.menta.libs.security.ownership.identity.extractor.EntityIdentityRequestExtractorStrategy
import com.menta.libs.security.ownership.owner.Owner
import javax.servlet.http.HttpServletRequest

class DefaultOwnerIdentityProvider(
    private val extractorStrategy: EntityIdentityRequestExtractorStrategy
) : OwnerIdentityProvider {
    override fun provideFrom(request: HttpServletRequest, owner: Owner): OwnerIdentity =
        OwnerIdentity(
            id = extractorStrategy.extract(request, owner),
            type = owner.type
        )
}

