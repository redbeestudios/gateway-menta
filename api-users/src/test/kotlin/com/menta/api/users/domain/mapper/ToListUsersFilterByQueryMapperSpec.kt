package com.menta.api.users.domain.mapper

import com.menta.api.users.customerId
import com.menta.api.users.domain.ListUsersFilterByQuery
import com.menta.api.users.domain.UserType.CUSTOMER
import com.menta.api.users.email
import com.menta.api.users.merchantId
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe

class ToListUsersFilterByQueryMapperSpec : FeatureSpec({

    val mapper = ToListUsersFilterByQueryMapper()

    feature("map query from filter params") {
        scenario("map with type") {
            mapper.mapFrom(
                type = CUSTOMER,
                email = null,
                customerId = null,
                merchantId = null,
                limit = null,
                next = null
            ) shouldBe ListUsersFilterByQuery(
                type = CUSTOMER,
                email = null,
                customerId = null,
                merchantId = null,
                search = ListUsersFilterByQuery.Search(
                    limit = null,
                    next = null
                )
            )
        }
        scenario("map with type and email") {
            mapper.mapFrom(
                type = CUSTOMER,
                email = email,
                customerId = null,
                merchantId = null,
                limit = null,
                next = null
            ) shouldBe ListUsersFilterByQuery(
                type = CUSTOMER,
                email = email,
                customerId = null,
                merchantId = null,
                search = ListUsersFilterByQuery.Search(
                    limit = null,
                    next = null
                )
            )
        }
        scenario("map with type and customerId") {
            mapper.mapFrom(
                type = CUSTOMER,
                email = email,
                customerId = customerId,
                merchantId = null,
                limit = null,
                next = null
            ) shouldBe ListUsersFilterByQuery(
                type = CUSTOMER,
                email = email,
                customerId = customerId,
                merchantId = null,
                search = ListUsersFilterByQuery.Search(
                    limit = null,
                    next = null
                )
            )
        }
        scenario("map with type and customerId and merchantId") {
            mapper.mapFrom(
                type = CUSTOMER,
                email = null,
                customerId = customerId,
                merchantId = merchantId,
                limit = null,
                next = null
            ) shouldBe ListUsersFilterByQuery(
                type = CUSTOMER,
                email = null,
                customerId = customerId,
                merchantId = merchantId,
                search = ListUsersFilterByQuery.Search(
                    limit = null,
                    next = null
                )
            )
        }
        scenario("map with type and limit") {
            mapper.mapFrom(
                type = CUSTOMER,
                email = null,
                customerId = null,
                merchantId = null,
                limit = 10,
                next = null
            ) shouldBe ListUsersFilterByQuery(
                type = CUSTOMER,
                email = null,
                customerId = null,
                merchantId = null,
                search = ListUsersFilterByQuery.Search(
                    limit = 10,
                    next = null
                )
            )
        }
        scenario("map with type and next") {
            mapper.mapFrom(
                type = CUSTOMER,
                email = null,
                customerId = null,
                merchantId = null,
                limit = null,
                next = "token"
            ) shouldBe ListUsersFilterByQuery(
                type = CUSTOMER,
                email = null,
                customerId = null,
                merchantId = null,
                search = ListUsersFilterByQuery.Search(
                    limit = null,
                    next = "token"
                )
            )
        }
    }
})
