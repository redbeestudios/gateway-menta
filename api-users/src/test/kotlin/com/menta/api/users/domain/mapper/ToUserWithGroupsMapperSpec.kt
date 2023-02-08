package com.menta.api.users.domain.mapper

import com.menta.api.users.createDate
import com.menta.api.users.customerId
import com.menta.api.users.domain.Group
import com.menta.api.users.domain.ListGroupByUserQuery
import com.menta.api.users.domain.ListGroupsByUserQueryResult
import com.menta.api.users.domain.User
import com.menta.api.users.domain.UserStatus
import com.menta.api.users.domain.UserType
import com.menta.api.users.domain.UserWithGroups
import com.menta.api.users.email
import com.menta.api.users.merchantId
import com.menta.api.users.updateDate
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import java.time.Instant
import java.util.Date

class ToUserWithGroupsMapperSpec : FeatureSpec({

    val mapper = ToUserWithGroupsMapper()

    feature("map user with groups from group query and groups result") {

        scenario("with query and result") {
            val query = ListGroupByUserQuery(
                user = User(
                    enabled = true,
                    status = UserStatus.CONFIRMED,
                    attributes = User.Attributes(
                        email = email,
                        customerId = customerId.toString(),
                        merchantId = merchantId.toString(),
                        type = UserType.MERCHANT
                    ),
                    audit = User.Audit(
                        creationDate = Date.from(Instant.ofEpochSecond(12345)),
                        updateDate = Date.from(Instant.ofEpochSecond(65678))
                    )
                ),
                search = ListGroupByUserQuery.Search(
                    limit = null,
                    next = null
                )
            )
            val result = ListGroupsByUserQueryResult(
                groups = listOf(
                    Group(
                        name = "name",
                        description = "description",
                        audit = Group.Audit(
                            creationDate = createDate,
                            updateDate = updateDate
                        )
                    )
                ),
                next = null
            )

            mapper.mapFrom(query, result) shouldBe UserWithGroups(
                user = query.user,
                groups = result.groups,
                next = null,
                limit = null
            )
        }
    }
})
