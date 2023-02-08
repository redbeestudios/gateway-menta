package com.menta.api.users.domain.mapper

import com.amazonaws.services.cognitoidp.model.AdminCreateUserResult
import com.amazonaws.services.cognitoidp.model.AdminGetUserResult
import com.amazonaws.services.cognitoidp.model.AttributeType
import com.menta.api.users.adapter.out.model.CognitoFieldName.CUSTOMER_ID
import com.menta.api.users.adapter.out.model.CognitoFieldName.EMAIL
import com.menta.api.users.adapter.out.model.CognitoFieldName.MERCHANT_ID
import com.menta.api.users.domain.User
import com.menta.api.users.domain.UserStatus
import com.menta.api.users.domain.UserType
import com.menta.api.users.shared.other.util.log.CompanionLogger
import org.springframework.stereotype.Component
import com.amazonaws.services.cognitoidp.model.UserType as CognitoUser

@Component
class ToUserMapper {

    fun mapFrom(cognitoUser: AdminGetUserResult, type: UserType): User =
        with(cognitoUser) {
            User(
                attributes = with(userAttributes) {
                    User.Attributes(
                        email = getField(EMAIL.fieldName)!!,
                        merchantId = getField(MERCHANT_ID.fieldName),
                        customerId = getField(CUSTOMER_ID.fieldName),
                        type = type
                    )
                },
                enabled = enabled,
                status = UserStatus.valueOf(userStatus.toString()),
                audit = User.Audit(
                    creationDate = userCreateDate,
                    updateDate = userLastModifiedDate
                )
            )
        }.log { info("user mapped: {}", it) }

    fun mapFrom(cognitoUser: AdminCreateUserResult, type: UserType): User =
        mapFrom(cognitoUser.user, type)

    fun mapFrom(cognitoUser: CognitoUser, type: UserType): User =
        with(cognitoUser) {
            User(
                attributes = with(attributes) {
                    User.Attributes(
                        email = getField(EMAIL.fieldName)!!,
                        merchantId = getField(MERCHANT_ID.fieldName),
                        customerId = getField(CUSTOMER_ID.fieldName),
                        type = type
                    )
                },
                enabled = enabled,
                status = UserStatus.valueOf(userStatus.toString()),
                audit = User.Audit(
                    creationDate = userCreateDate,
                    updateDate = userLastModifiedDate
                )
            )
        }.log { info("user mapped: {}", it) }

    private fun List<AttributeType>?.getField(fieldName: String) =
        this?.find { it.name == fieldName }?.value

    companion object : CompanionLogger()
}
