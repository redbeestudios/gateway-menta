package com.menta.api.users.adapter.out.model.mapper

import com.amazonaws.services.cognitoidp.model.AdminCreateUserRequest
import com.amazonaws.services.cognitoidp.model.AttributeType
import com.amazonaws.services.cognitoidp.model.DeliveryMediumType
import com.menta.api.users.adapter.out.model.CognitoFieldName
import com.menta.api.users.customerId
import com.menta.api.users.domain.NewUser
import com.menta.api.users.domain.UserType.CUSTOMER
import com.menta.api.users.domain.UserType.MERCHANT
import com.menta.api.users.domain.UserType.SUPPORT
import com.menta.api.users.email
import com.menta.api.users.merchantId
import com.menta.api.users.shared.other.config.cognito.CognitoConfigurationProperties.Provider.UserPool
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe

class ToAdminCreateUserRequestMapperSpec : FeatureSpec({

    val mapper = ToAdminCreateUserRequestMapper()

    feature("map cognito create request from user pool aware") {
        scenario("map support") {
            mapper.mapFrom(
                NewUser(
                    type = SUPPORT,
                    attributes = NewUser.Attributes(
                        email = email,
                        customerId = null,
                        merchantId = null
                    )
                ),
                userPool = UserPool(
                    code = "us-east-1_PWeF8HOR0",
                    clientId = "merchant"
                )
            ) shouldBe AdminCreateUserRequest()
                .withDesiredDeliveryMediums(DeliveryMediumType.EMAIL)
                .withUsername(email)
                .withUserPoolId("us-east-1_PWeF8HOR0")
                .withUserAttributes(
                    listOfNotNull(
                        AttributeType().withName(CognitoFieldName.EMAIL.fieldName).withValue(email),
                        AttributeType().withName(CognitoFieldName.EMAIL_VERIFIED.fieldName).withValue("true")
                    )
                )
        }
        scenario("map customer") {
            mapper.mapFrom(
                NewUser(
                    type = CUSTOMER,
                    attributes = NewUser.Attributes(
                        email = email,
                        customerId = customerId,
                        merchantId = null
                    )
                ),
                UserPool(
                    code = "us-east-1_PWeF8HOR0",
                    clientId = "4bo11klmou1r2ujqm227p086os"
                )
            ) shouldBe AdminCreateUserRequest()
                .withDesiredDeliveryMediums(DeliveryMediumType.EMAIL)
                .withUsername(email)
                .withUserPoolId("us-east-1_PWeF8HOR0")
                .withUserAttributes(
                    listOfNotNull(
                        AttributeType().withName(CognitoFieldName.EMAIL.fieldName).withValue(email),
                        AttributeType().withName(CognitoFieldName.EMAIL_VERIFIED.fieldName).withValue("true"),
                        AttributeType().withName(CognitoFieldName.CUSTOMER_ID.fieldName)
                            .withValue(customerId.toString())
                    )
                )
        }
        scenario("map merchant") {
            mapper.mapFrom(
                NewUser(
                    type = MERCHANT,
                    attributes = NewUser.Attributes(
                        email = email,
                        customerId = customerId,
                        merchantId = merchantId
                    )
                ),
                UserPool(
                    code = "us-east-1_PWeF8HOR0",
                    clientId = "4bo11klmou1r2ujqm227p086os"
                )
            ) shouldBe AdminCreateUserRequest()
                .withDesiredDeliveryMediums(DeliveryMediumType.EMAIL)
                .withUsername(email)
                .withUserPoolId("us-east-1_PWeF8HOR0")
                .withUserAttributes(
                    listOfNotNull(
                        AttributeType().withName(CognitoFieldName.EMAIL.fieldName).withValue(email),
                        AttributeType().withName(CognitoFieldName.EMAIL_VERIFIED.fieldName).withValue("true"),
                        AttributeType().withName(CognitoFieldName.MERCHANT_ID.fieldName)
                            .withValue(merchantId.toString()),
                        AttributeType().withName(CognitoFieldName.CUSTOMER_ID.fieldName)
                            .withValue(customerId.toString())
                    )
                )
        }
    }
})
