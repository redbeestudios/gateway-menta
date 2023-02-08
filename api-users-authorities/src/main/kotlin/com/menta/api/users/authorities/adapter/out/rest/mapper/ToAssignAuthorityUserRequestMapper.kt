package com.menta.api.users.authorities.adapter.out.rest.mapper

import com.menta.api.users.authorities.adapter.out.rest.model.AssignAuthorityUserRequest
import com.menta.api.users.authorities.shared.other.util.log.CompanionLogger
import org.springframework.stereotype.Component

@Component
class ToAssignAuthorityUserRequestMapper {

    fun mapFrom(authority: String) =
        AssignAuthorityUserRequest(
            name = authority
        ).log { info("assignAuthorityUserRequest mapped successfully") }

    companion object : CompanionLogger()
}
