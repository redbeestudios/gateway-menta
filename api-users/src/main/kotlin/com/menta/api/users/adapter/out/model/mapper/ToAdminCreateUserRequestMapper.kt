package com.menta.api.users.adapter.out.model.mapper

import com.amazonaws.services.cognitoidp.model.AdminCreateUserRequest
import com.amazonaws.services.cognitoidp.model.AttributeType
import com.amazonaws.services.cognitoidp.model.DeliveryMediumType.EMAIL
import com.menta.api.users.adapter.out.model.CognitoFieldName
import com.menta.api.users.domain.NewUser
import com.menta.api.users.shared.other.config.cognito.CognitoConfigurationProperties.Provider.UserPool
import com.menta.api.users.shared.other.util.log.CompanionLogger
import org.springframework.stereotype.Component

@Component
class ToAdminCreateUserRequestMapper {
    fun mapFrom(newUser: NewUser, userPool: UserPool): AdminCreateUserRequest =
        with(newUser) {
            AdminCreateUserRequest()
                .withDesiredDeliveryMediums(EMAIL)
                .withUsername(attributes.email)
                .withUserPoolId(userPool.code)
                .withUserAttributes(attributes.map())
        }.log { info("create user request mapped: {}", it) }

    private fun NewUser.Attributes.map() =
        listOfNotNull(
            AttributeType().withName(CognitoFieldName.EMAIL.fieldName).withValue(email),
            AttributeType().withName(CognitoFieldName.EMAIL_VERIFIED.fieldName)
                .withValue(CognitoFieldName.EMAIL_VERIFIED_VALUE),
            merchantId?.let {
                AttributeType().withName(CognitoFieldName.MERCHANT_ID.fieldName).withValue(it.toString())
            },
            customerId?.let {
                AttributeType().withName(CognitoFieldName.CUSTOMER_ID.fieldName).withValue(it.toString())
            }
        )

    companion object : CompanionLogger()
}
