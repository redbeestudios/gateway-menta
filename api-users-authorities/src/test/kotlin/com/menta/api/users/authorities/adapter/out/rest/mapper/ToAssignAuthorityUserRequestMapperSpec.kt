package com.menta.api.users.authorities.adapter.out.rest.mapper

import com.menta.api.users.authorities.adapter.out.rest.model.AssignAuthorityUserRequest
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks

class ToAssignAuthorityUserRequestMapperSpec : FeatureSpec({

    val mapper = ToAssignAuthorityUserRequestMapper()

    beforeEach { clearAllMocks() }

    feature("mapFrom authority") {
        scenario("successful map") {
            val authority = "Payment::Create"
            mapper.mapFrom(authority) shouldBe AssignAuthorityUserRequest(
                name = "Payment::Create"
            )
        }
    }
})
