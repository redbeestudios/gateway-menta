package com.menta.api.users.adapter.out.model.mapper

import com.amazonaws.services.cognitoidp.model.ListUsersRequest
import com.menta.api.users.aUserPool
import com.menta.api.users.customerId
import com.menta.api.users.domain.ListUsersFilterByQuery
import com.menta.api.users.domain.UserType.MERCHANT
import com.menta.api.users.email
import com.menta.api.users.merchantId
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe

class ToListUsersRequestMapperSpec : FeatureSpec({

    val mapper = ToListUsersRequestMapper()

    feature("map cognito list users request from list users filter by query") {
        val pool = aUserPool()

        scenario("with required filters") {
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

            mapper.mapFrom(
                query, pool
            ) shouldBe ListUsersRequest()
                .withUserPoolId("us-east-1_PWeF8HOR0")
        }
        scenario("with email") {
            val query = ListUsersFilterByQuery(
                type = MERCHANT,
                email = email,
                customerId = null,
                merchantId = null,
                search = ListUsersFilterByQuery.Search(
                    limit = null,
                    next = null
                )
            )

            mapper.mapFrom(
                query, pool
            ) shouldBe ListUsersRequest()
                .withUserPoolId("us-east-1_PWeF8HOR0")
                .withFilter("email = \"${email}\"")
        }
        scenario("with customerId") {
            val query = ListUsersFilterByQuery(
                type = MERCHANT,
                email = null,
                customerId = customerId,
                merchantId = null,
                search = ListUsersFilterByQuery.Search(
                    limit = null,
                    next = null
                )
            )

            mapper.mapFrom(
                query, pool
            ) shouldBe ListUsersRequest()
                .withUserPoolId("us-east-1_PWeF8HOR0")
                .withFilter("family_name = \"${customerId}\"")
        }
        scenario("with merchantId") {
            val query = ListUsersFilterByQuery(
                type = MERCHANT,
                email = null,
                customerId = null,
                merchantId = merchantId,
                search = ListUsersFilterByQuery.Search(
                    limit = null,
                    next = null
                )
            )

            mapper.mapFrom(
                query, pool
            ) shouldBe ListUsersRequest()
                .withUserPoolId("us-east-1_PWeF8HOR0")
                .withFilter("given_name = \"${merchantId}\"")
        }
        scenario("with limit") {
            val query = ListUsersFilterByQuery(
                type = MERCHANT,
                email = null,
                customerId = null,
                merchantId = null,
                search = ListUsersFilterByQuery.Search(
                    limit = 10,
                    next = null
                )
            )
            mapper.mapFrom(
                query, pool
            ) shouldBe ListUsersRequest()
                .withUserPoolId("us-east-1_PWeF8HOR0")
                .withLimit(10)
        }
        scenario("with pagination token") {
            val query = ListUsersFilterByQuery(
                type = MERCHANT,
                email = null,
                customerId = null,
                merchantId = null,
                search = ListUsersFilterByQuery.Search(
                    limit = null,
                    next = "token"
                )
            )
            mapper.mapFrom(
                query, pool
            ) shouldBe ListUsersRequest()
                .withUserPoolId("us-east-1_PWeF8HOR0")
                .withPaginationToken("token")
        }
    }
})
