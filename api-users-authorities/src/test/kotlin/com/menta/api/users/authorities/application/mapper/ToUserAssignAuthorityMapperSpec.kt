package com.menta.api.users.authorities.application.mapper

import com.menta.api.users.authorities.anUser
import com.menta.api.users.authorities.domain.UserAssignAuthority
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks

class ToUserAssignAuthorityMapperSpec : FeatureSpec({

    val mapper = ToUserAssignAuthorityMapper()

    beforeEach { clearAllMocks() }

    feature("mapFrom user and authority") {
        val user = anUser
        val authority = "Payment::Create"

        scenario("map") {
            mapper.mapFrom(user, authority) shouldBe UserAssignAuthority(
                user = user.attributes.email,
                type = user.attributes.type,
                authority = authority
            )
        }
    }
})
