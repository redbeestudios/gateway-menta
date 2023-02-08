package com.menta.libs.security.ownership.identity.validator

import com.menta.libs.security.ownership.identity.validator.exception.MissingEntityIdentityValidator
import com.menta.libs.security.ownership.owner.Owner
import com.menta.libs.security.requesterUser.model.RequesterUser
import com.menta.libs.security.requesterUser.model.RequesterUser.UserType
import javax.servlet.http.HttpServletRequest

class EntityIdentityValidatorStrategy(
    private val entityIdentityValidators: Map<UserType, EntityIdentityValidator>
) : EntityIdentityValidator {
    override fun validate(source: HttpServletRequest, owner: Owner, requesterUser: RequesterUser) {
        entityIdentityValidators[requesterUser.type]?.validate(source, owner, requesterUser)
            ?: throw MissingEntityIdentityValidator(requesterUser.type)
    }
}
