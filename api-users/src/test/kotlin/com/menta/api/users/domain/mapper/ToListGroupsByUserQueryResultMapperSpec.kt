package com.menta.api.users.domain.mapper

import com.amazonaws.services.cognitoidp.model.AdminListGroupsForUserResult
import com.amazonaws.services.cognitoidp.model.GroupType
import com.menta.api.users.createDate
import com.menta.api.users.domain.ListGroupsByUserQueryResult
import com.menta.api.users.updateDate
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe

class ToListGroupsByUserQueryResultMapperSpec : FeatureSpec({

    val mapper = ToListGroupsByUserQueryResultMapper()

    feature("map list groups by user from admin list groups for user result") {

        scenario("map") {
            val adminListGroupsForUserResult = AdminListGroupsForUserResult()
                .withNextToken("token")
                .withGroups(
                    GroupType()
                        .withGroupName("name")
                        .withDescription("description")
                        .withCreationDate(createDate)
                        .withLastModifiedDate(updateDate)

                )

            mapper.mapFrom(adminListGroupsForUserResult) shouldBe ListGroupsByUserQueryResult(
                groups = listOf(
                    com.menta.api.users.domain.Group(
                        name = "name",
                        description = "description",
                        audit = com.menta.api.users.domain.Group.Audit(
                            creationDate = createDate,
                            updateDate = updateDate
                        )
                    )
                ),
                next = "token"
            )
        }
    }
})
