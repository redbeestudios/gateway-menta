package com.menta.api.users.domain.mapper

import com.menta.api.users.customerId
import com.menta.api.users.domain.ListGroupByUserQuery
import com.menta.api.users.domain.User
import com.menta.api.users.domain.UserStatus.CONFIRMED
import com.menta.api.users.domain.UserType.MERCHANT
import com.menta.api.users.email
import com.menta.api.users.merchantId
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import java.time.Instant
import java.util.Date

class ToListGroupsByUserQueryMapperSpec : FeatureSpec({

    val mapper = ToListGroupByUserQueryMapper()

    feature("map list group by user query from user and search params") {

        scenario("map with user") {
            val user = User(
                enabled = true,
                status = CONFIRMED,
                attributes = User.Attributes(
                    email = email,
                    customerId = customerId.toString(),
                    merchantId = merchantId.toString(),
                    type = MERCHANT
                ),
                audit = User.Audit(
                    creationDate = Date.from(Instant.ofEpochSecond(12345)),
                    updateDate = Date.from(Instant.ofEpochSecond(65678))
                )
            )
            mapper.mapFrom(user = user, next = null, limit = null) shouldBe ListGroupByUserQuery(
                user = user,
                search = ListGroupByUserQuery.Search(
                    limit = null,
                    next = null
                )
            )
        }
        scenario("map with search params") {
            val user = User(
                enabled = true,
                status = CONFIRMED,
                attributes = User.Attributes(
                    email = email,
                    customerId = customerId.toString(),
                    merchantId = merchantId.toString(),
                    type = MERCHANT
                ),
                audit = User.Audit(
                    creationDate = Date.from(Instant.ofEpochSecond(12345)),
                    updateDate = Date.from(Instant.ofEpochSecond(65678))
                )
            )
            mapper.mapFrom(user = user, next = "token", limit = 10) shouldBe ListGroupByUserQuery(
                user = user,
                search = ListGroupByUserQuery.Search(
                    limit = 10,
                    next = "token"
                )
            )
        }
    }
})
