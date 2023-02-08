package com.menta.api.users.domain.mapper

import com.amazonaws.services.cognitoidp.model.AdminCreateUserResult
import com.amazonaws.services.cognitoidp.model.AdminGetUserResult
import com.amazonaws.services.cognitoidp.model.AttributeType
import com.amazonaws.services.cognitoidp.model.UserStatusType.ARCHIVED
import com.amazonaws.services.cognitoidp.model.UserStatusType.COMPROMISED
import com.amazonaws.services.cognitoidp.model.UserStatusType.CONFIRMED
import com.amazonaws.services.cognitoidp.model.UserStatusType.FORCE_CHANGE_PASSWORD
import com.amazonaws.services.cognitoidp.model.UserStatusType.RESET_REQUIRED
import com.amazonaws.services.cognitoidp.model.UserStatusType.UNCONFIRMED
import com.amazonaws.services.cognitoidp.model.UserStatusType.UNKNOWN
import com.menta.api.users.domain.User
import com.menta.api.users.domain.UserStatus
import com.menta.api.users.domain.UserType.CUSTOMER
import com.menta.api.users.domain.UserType.MERCHANT
import com.menta.api.users.domain.UserType.SUPPORT
import com.menta.api.users.email
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import java.time.Instant
import java.util.Date

class ToUserMapperSpec : FeatureSpec({

    val mapper = ToUserMapper()

    feature("map user from cognito get user response") {
        val createDate = Date.from(Instant.ofEpochSecond(1234))
        val updateDate = Date.from(Instant.ofEpochSecond(5667))

        scenario("customer user") {
            val cognitoUser = AdminGetUserResult()
                .withUsername("user@user.com")
                .withEnabled(true)
                .withUserStatus(CONFIRMED)
                .withUserCreateDate(createDate)
                .withUserLastModifiedDate(updateDate)
                .withUserAttributes(
                    listOf(
                        AttributeType().withName("email").withValue(email),
                        AttributeType().withName("family_name").withValue("a customerId")
                    )
                )

            val type = CUSTOMER

            mapper.mapFrom(cognitoUser, type) shouldBe
                User(
                    attributes = User.Attributes(
                        email = email,
                        merchantId = null,
                        customerId = "a customerId",
                        type = type
                    ),
                    enabled = true,
                    status = UserStatus.CONFIRMED,
                    audit = User.Audit(
                        creationDate = createDate,
                        updateDate = updateDate
                    )
                )
        }

        scenario("merchant user") {
            val cognitoUser = AdminGetUserResult()
                .withUsername("user@user.com")
                .withEnabled(true)
                .withUserStatus(CONFIRMED)
                .withUserCreateDate(createDate)
                .withUserLastModifiedDate(updateDate)
                .withUserAttributes(
                    listOf(
                        AttributeType().withName("email").withValue(email),
                        AttributeType().withName("given_name").withValue("a merchantId"),
                        AttributeType().withName("family_name").withValue("a customerId")
                    )
                )

            val type = MERCHANT

            mapper.mapFrom(cognitoUser, type) shouldBe
                User(
                    attributes = User.Attributes(
                        email = email,
                        merchantId = "a merchantId",
                        customerId = "a customerId",
                        type = type
                    ),
                    enabled = true,
                    status = UserStatus.CONFIRMED,
                    audit = User.Audit(
                        creationDate = createDate,
                        updateDate = updateDate
                    )
                )
        }

        scenario("support user") {
            val cognitoUser = AdminGetUserResult()
                .withUsername("user@user.com")
                .withEnabled(true)
                .withUserStatus(CONFIRMED)
                .withUserCreateDate(createDate)
                .withUserLastModifiedDate(updateDate)
                .withUserAttributes(
                    listOf(AttributeType().withName("email").withValue(email))
                )

            val type = SUPPORT

            mapper.mapFrom(cognitoUser, type) shouldBe
                User(
                    attributes = User.Attributes(
                        email = email,
                        customerId = null,
                        merchantId = null,
                        type = type
                    ),
                    enabled = true,
                    status = UserStatus.CONFIRMED,
                    audit = User.Audit(
                        creationDate = createDate,
                        updateDate = updateDate
                    )
                )
        }

        scenario("status mapped") {
            forAll(
                row(CONFIRMED, UserStatus.CONFIRMED),
                row(ARCHIVED, UserStatus.ARCHIVED),
                row(UNCONFIRMED, UserStatus.UNCONFIRMED),
                row(UNKNOWN, UserStatus.UNKNOWN),
                row(COMPROMISED, UserStatus.COMPROMISED),
                row(FORCE_CHANGE_PASSWORD, UserStatus.FORCE_CHANGE_PASSWORD),
                row(RESET_REQUIRED, UserStatus.RESET_REQUIRED)
            ) { cognitoType, userType ->
                val cognitoUser = AdminGetUserResult()
                    .withUsername(email)
                    .withEnabled(true)
                    .withUserStatus(cognitoType)
                    .withUserCreateDate(createDate)
                    .withUserLastModifiedDate(updateDate)
                    .withUserAttributes(AttributeType().withName("email").withValue(email))

                val type = SUPPORT

                mapper.mapFrom(cognitoUser, type) shouldBe
                    User(
                        attributes = User.Attributes(
                            email = email,
                            customerId = null,
                            merchantId = null,
                            type = type
                        ),
                        enabled = true,
                        status = userType,
                        audit = User.Audit(
                            creationDate = createDate,
                            updateDate = updateDate
                        )
                    )
            }
        }
    }

    feature("map user from cognito create user response") {
        val createDate = Date.from(Instant.ofEpochSecond(1234))
        val updateDate = Date.from(Instant.ofEpochSecond(5667))

        scenario("merchant user") {
            val cognitoUser = AdminCreateUserResult()
                .withUser(
                    com.amazonaws.services.cognitoidp.model.UserType()
                        .withUsername(email)
                        .withEnabled(true)
                        .withUserStatus(CONFIRMED)
                        .withUserCreateDate(createDate)
                        .withUserLastModifiedDate(updateDate)
                        .withAttributes(
                            listOf(
                                AttributeType().withName("email").withValue(email),
                                AttributeType().withName("given_name").withValue("a merchantId"),
                                AttributeType().withName("family_name").withValue("a customerId")
                            )
                        )
                )

            val type = MERCHANT

            mapper.mapFrom(cognitoUser, type) shouldBe
                User(
                    attributes = User.Attributes(
                        email = email,
                        merchantId = "a merchantId",
                        customerId = "a customerId",
                        type = type
                    ),
                    enabled = true,
                    status = UserStatus.CONFIRMED,
                    audit = User.Audit(
                        creationDate = createDate,
                        updateDate = updateDate
                    )
                )
        }

        scenario("customer user") {
            val cognitoUser = AdminCreateUserResult()
                .withUser(
                    com.amazonaws.services.cognitoidp.model.UserType()
                        .withUsername(email)
                        .withEnabled(true)
                        .withUserStatus(CONFIRMED)
                        .withUserCreateDate(createDate)
                        .withUserLastModifiedDate(updateDate)
                        .withAttributes(
                            listOf(
                                AttributeType().withName("email").withValue(email),
                                AttributeType().withName("family_name").withValue("a customerId")
                            )
                        )
                )

            val type = CUSTOMER

            mapper.mapFrom(cognitoUser, type) shouldBe
                User(
                    attributes = User.Attributes(
                        email = email,
                        merchantId = null,
                        customerId = "a customerId",
                        type = type
                    ),
                    enabled = true,
                    status = UserStatus.CONFIRMED,
                    audit = User.Audit(
                        creationDate = createDate,
                        updateDate = updateDate
                    )
                )
        }

        scenario("support user") {
            val cognitoUser = AdminCreateUserResult()
                .withUser(
                    com.amazonaws.services.cognitoidp.model.UserType()
                        .withUsername(email)
                        .withEnabled(true)
                        .withUserStatus(CONFIRMED)
                        .withUserCreateDate(createDate)
                        .withUserLastModifiedDate(updateDate)
                        .withAttributes(
                            listOf(AttributeType().withName("email").withValue(email))
                        )
                )

            val type = SUPPORT

            mapper.mapFrom(cognitoUser, type) shouldBe
                User(
                    attributes = User.Attributes(
                        email = email,
                        merchantId = null,
                        customerId = null,
                        type = type
                    ),
                    enabled = true,
                    status = UserStatus.CONFIRMED,
                    audit = User.Audit(
                        creationDate = createDate,
                        updateDate = updateDate
                    )
                )
        }

        scenario("status mapped") {
            forAll(
                row(CONFIRMED, UserStatus.CONFIRMED),
                row(ARCHIVED, UserStatus.ARCHIVED),
                row(UNCONFIRMED, UserStatus.UNCONFIRMED),
                row(UNKNOWN, UserStatus.UNKNOWN),
                row(COMPROMISED, UserStatus.COMPROMISED),
                row(FORCE_CHANGE_PASSWORD, UserStatus.FORCE_CHANGE_PASSWORD),
                row(RESET_REQUIRED, UserStatus.RESET_REQUIRED)
            ) { cognitoType, userType ->
                val cognitoUser = AdminCreateUserResult()
                    .withUser(
                        com.amazonaws.services.cognitoidp.model.UserType()
                            .withUsername(email)
                            .withEnabled(true)
                            .withUserStatus(cognitoType)
                            .withUserCreateDate(createDate)
                            .withUserLastModifiedDate(updateDate)
                            .withAttributes(AttributeType().withName("email").withValue(email))
                    )
                val type = SUPPORT

                mapper.mapFrom(cognitoUser, type) shouldBe
                    User(
                        attributes = User.Attributes(
                            email = email,
                            customerId = null,
                            merchantId = null,
                            type = type
                        ),
                        enabled = true,
                        status = userType,
                        audit = User.Audit(
                            creationDate = createDate,
                            updateDate = updateDate
                        )
                    )
            }
        }
    }
})
