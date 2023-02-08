package com.menta.api.users.adapter.out.model.mapper

import com.amazonaws.services.cognitoidp.model.AdminAddUserToGroupRequest
import com.menta.api.users.createDate
import com.menta.api.users.customerId
import com.menta.api.users.domain.GroupAssignation
import com.menta.api.users.domain.User
import com.menta.api.users.domain.UserStatus.CONFIRMED
import com.menta.api.users.domain.UserType.MERCHANT
import com.menta.api.users.email
import com.menta.api.users.merchantId
import com.menta.api.users.shared.other.config.cognito.CognitoConfigurationProperties.Provider.UserPool
import com.menta.api.users.updateDate
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe

class ToAdminAddUserToGroupRequestMapperSpec : FeatureSpec({

    val mapper = ToAdminAddUserToGroupRequestMapper()

    feature("map request from group assignation and user pool") {

        scenario("map") {
            val groupAssignation = GroupAssignation(
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
                group = GroupAssignation.Group(
                    name = "Payment::Create"
                )
            )

            val pool = UserPool(
                code = "us-east-1_PWeF8HOR0",
                clientId = "4bo11klmou1r2ujqm227p086os"
            )

            mapper.mapFrom(groupAssignation, pool) shouldBe
                AdminAddUserToGroupRequest()
                    .withGroupName("Payment::Create")
                    .withUsername(email)
                    .withUserPoolId("us-east-1_PWeF8HOR0")
        }
    }
})
