package com.menta.api.users.domain.mapper

import com.menta.api.users.domain.ListUserPage
import com.menta.api.users.domain.ListUsersFilterByQuery
import com.menta.api.users.domain.ListUsersFilterByQueryResult
import com.menta.api.users.domain.User
import com.menta.api.users.domain.UserStatus
import com.menta.api.users.domain.UserType.MERCHANT
import com.menta.api.users.domain.UserType.SUPPORT
import com.menta.api.users.email
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import java.time.Instant
import java.util.Date

class ToListUsersMapperSpec : FeatureSpec({

    val mapper = ToListUsersMapper()

    feature("map user from ListUsersFilterByQuery and ListUsersFilterByQueryResult") {
        val createDate = Date.from(Instant.ofEpochSecond(1234))
        val updateDate = Date.from(Instant.ofEpochSecond(5667))

        scenario("successful map") {
            val query = ListUsersFilterByQuery(
                type = MERCHANT,
                email = null,
                customerId = null,
                merchantId = null,
                search = ListUsersFilterByQuery.Search(
                    limit = null,
                    next = null
                )
            )

            val result = ListUsersFilterByQueryResult(
                users = listOf(
                    User(
                        attributes = User.Attributes(
                            email = email,
                            merchantId = null,
                            customerId = null,
                            type = SUPPORT
                        ),
                        enabled = true,
                        status = UserStatus.CONFIRMED,
                        audit = User.Audit(
                            creationDate = createDate,
                            updateDate = updateDate
                        )
                    )
                ),
                next = "token"
            )

            mapper.mapFrom(query, result) shouldBe
                ListUserPage(
                    users = result.users,
                    next = result.next,
                    limit = query.search.limit
                )
        }
    }
})
