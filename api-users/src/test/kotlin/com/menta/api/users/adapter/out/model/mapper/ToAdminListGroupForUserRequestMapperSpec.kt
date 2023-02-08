package com.menta.api.users.adapter.out.model.mapper

import com.amazonaws.services.cognitoidp.model.AdminListGroupsForUserRequest
import com.menta.api.users.adapter.`in`.model.mapper.ToAdminListGroupForUserRequestMapper
import com.menta.api.users.createDate
import com.menta.api.users.customerId
import com.menta.api.users.domain.ListGroupByUserQuery
import com.menta.api.users.domain.User
import com.menta.api.users.domain.UserStatus.CONFIRMED
import com.menta.api.users.domain.UserType.MERCHANT
import com.menta.api.users.email
import com.menta.api.users.merchantId
import com.menta.api.users.shared.other.config.cognito.CognitoConfigurationProperties.Provider.UserPool
import com.menta.api.users.updateDate
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe

class ToAdminListGroupForUserRequestMapperSpec : FeatureSpec({

    val mapper = ToAdminListGroupForUserRequestMapper()

    feature("map request from list group user query and user pool") {

        scenario("map") {
            val query = ListGroupByUserQuery(
                user = User(
                    attributes = User.Attributes(
                        email = email,
                        merchantId = merchantId.toString(),
                        customerId = customerId.toString(),
                        type = MERCHANT
                    ),
                    enabled = true,
                    status = CONFIRMED,
                    audit = User.Audit(
                        creationDate = createDate,
                        updateDate = updateDate
                    )
                ),
                search = ListGroupByUserQuery.Search(
                    limit = null,
                    next = null
                )
            )
            val pool = UserPool(
                code = "us-east-1_PWeF8HOR0",
                clientId = "4bo11klmou1r2ujqm227p086os"
            )

            mapper.mapFrom(query, pool) shouldBe
                AdminListGroupsForUserRequest()
                    .withUserPoolId("us-east-1_PWeF8HOR0")
                    .withUsername(email)
        }
        scenario("map with search fields") {
            val query = ListGroupByUserQuery(
                user = User(
                    attributes = User.Attributes(
                        email = email,
                        merchantId = merchantId.toString(),
                        customerId = customerId.toString(),
                        type = MERCHANT
                    ),
                    enabled = true,
                    status = CONFIRMED,
                    audit = User.Audit(
                        creationDate = createDate,
                        updateDate = updateDate
                    )
                ),
                search = ListGroupByUserQuery.Search(
                    limit = 10,
                    next = "token"
                )
            )
            val pool = UserPool(
                code = "us-east-1_PWeF8HOR0",
                clientId = "4bo11klmou1r2ujqm227p086os"
            )

            mapper.mapFrom(query, pool) shouldBe
                AdminListGroupsForUserRequest()
                    .withUserPoolId("us-east-1_PWeF8HOR0")
                    .withUsername(email)
                    .withLimit(10)
                    .withNextToken("token")
        }
    }
})
