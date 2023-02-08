package com.menta.api.users.domain.mapper

import com.menta.api.users.aUser
import com.menta.api.users.adapter.`in`.model.AssignGroupRequest
import com.menta.api.users.domain.GroupAssignation
import com.menta.api.users.domain.GroupAssignation.Group
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe

class ToGroupAssignationMapperSpec : FeatureSpec({

    val mapper = ToGroupAssignationMapper()

    feature("map group assignation from request and user") {

        scenario("map") {

            val request = AssignGroupRequest(name = "Payment::Create")
            val user = aUser()

            mapper.mapFrom(request, user) shouldBe
                GroupAssignation(
                    user = user,
                    group = Group(
                        name = request.name
                    )
                )
        }
    }
})
