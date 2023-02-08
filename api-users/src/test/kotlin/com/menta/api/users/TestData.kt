package com.menta.api.users

import com.amazonaws.services.cognitoidp.model.AdminAddUserToGroupRequest
import com.amazonaws.services.cognitoidp.model.AdminCreateUserRequest
import com.amazonaws.services.cognitoidp.model.AdminCreateUserResult
import com.amazonaws.services.cognitoidp.model.AdminDisableUserRequest
import com.amazonaws.services.cognitoidp.model.AdminDisableUserResult
import com.amazonaws.services.cognitoidp.model.AdminGetUserRequest
import com.amazonaws.services.cognitoidp.model.AdminGetUserResult
import com.amazonaws.services.cognitoidp.model.AdminListGroupsForUserResult
import com.amazonaws.services.cognitoidp.model.AdminSetUserPasswordRequest
import com.amazonaws.services.cognitoidp.model.AdminSetUserPasswordResult
import com.amazonaws.services.cognitoidp.model.AttributeType
import com.amazonaws.services.cognitoidp.model.ConfirmForgotPasswordRequest
import com.amazonaws.services.cognitoidp.model.ConfirmForgotPasswordResult
import com.amazonaws.services.cognitoidp.model.DeliveryMediumType.EMAIL
import com.amazonaws.services.cognitoidp.model.ForgotPasswordRequest
import com.amazonaws.services.cognitoidp.model.ForgotPasswordResult
import com.amazonaws.services.cognitoidp.model.GroupType
import com.amazonaws.services.cognitoidp.model.InternalErrorException
import com.amazonaws.services.cognitoidp.model.InvalidParameterException
import com.amazonaws.services.cognitoidp.model.ListUsersRequest
import com.amazonaws.services.cognitoidp.model.ListUsersResult
import com.amazonaws.services.cognitoidp.model.NotAuthorizedException
import com.amazonaws.services.cognitoidp.model.ResourceNotFoundException
import com.amazonaws.services.cognitoidp.model.TooManyRequestsException
import com.amazonaws.services.cognitoidp.model.UserNotFoundException
import com.amazonaws.services.cognitoidp.model.UserStatusType
import com.menta.api.users.adapter.`in`.model.UserResponse
import com.menta.api.users.adapter.out.model.CognitoFieldName
import com.menta.api.users.domain.ConfirmForgotPasswordUser
import com.menta.api.users.domain.Group
import com.menta.api.users.domain.GroupAssignation
import com.menta.api.users.domain.ListGroupByUserQuery
import com.menta.api.users.domain.ListGroupsByUserQueryResult
import com.menta.api.users.domain.ListUserPage
import com.menta.api.users.domain.ListUsersFilterByQuery
import com.menta.api.users.domain.ListUsersFilterByQueryResult
import com.menta.api.users.domain.NewUser
import com.menta.api.users.domain.SetUserPassword
import com.menta.api.users.domain.User
import com.menta.api.users.domain.UserStatus
import com.menta.api.users.domain.UserStatus.CONFIRMED
import com.menta.api.users.domain.UserType.MERCHANT
import com.menta.api.users.domain.UserType.SUPPORT
import com.menta.api.users.domain.UserWithGroups
import com.menta.api.users.shared.other.config.cognito.CognitoConfigurationProperties.Provider.UserPool
import com.menta.api.users.shared.other.error.model.ApplicationError
import com.menta.api.users.shared.other.error.model.ApplicationError.Companion.resourceNotFound
import com.menta.api.users.shared.other.error.model.ApplicationError.Companion.unauthorizedUser
import com.menta.api.users.shared.other.error.model.ApplicationError.Companion.unhandledException
import com.menta.api.users.shared.other.error.model.ApplicationError.Companion.userNotFound
import io.kotest.data.headers
import io.kotest.data.row
import io.kotest.data.table
import java.time.Instant
import java.time.LocalDateTime
import java.time.Month
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.Date
import java.util.UUID

val customerId = UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389b")
val merchantId = UUID.fromString("669ed2f9-4c04-4c85-8b48-1a8fd67de963")
val email = "email@menta.com"
val password = "O9@mv0&J@8r4"
val createDate = Date.from(Instant.ofEpochSecond(1234))
val updateDate = Date.from(Instant.ofEpochSecond(5667))

fun aUser() =
    User(
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

fun aUserPool() =
    UserPool(
        code = "us-east-1_PWeF8HOR0",
        clientId = "4bo11klmou1r2ujqm227p086os"
    )

fun aNewUser() = NewUser(
    type = MERCHANT,
    attributes = NewUser.Attributes(
        email = email,
        customerId = customerId,
        merchantId = merchantId
    )
)

fun aSetUserPassword() = SetUserPassword(
    type = MERCHANT,
    email = email,
    password = "$password",
    permanent = false
)

fun aUserResponse() =
    UserResponse(
        attributes = UserResponse.Attributes(
            email = email,
            merchantId = merchantId.toString(),
            customerId = customerId.toString(),
            type = MERCHANT
        ),
        status = CONFIRMED,
        audit = UserResponse.Audit(
            creationDate = createDate,
            updateDate = updateDate
        )
    )

fun aFindUserRequest(): AdminGetUserRequest =
    AdminGetUserRequest()
        .withUserPoolId("user pool id")
        .withUsername("a username")

fun aFindUserResponse(): AdminGetUserResult =
    AdminGetUserResult()
        .withEnabled(true)
        .withUserStatus(UserStatusType.CONFIRMED)
        .withUsername("a user name")
        .withUserCreateDate(Date.from(Instant.ofEpochSecond(12345)))
        .withUserLastModifiedDate(Date.from(Instant.ofEpochSecond(65678)))

fun aCreateUserRequest() = AdminCreateUserRequest()
    .withDesiredDeliveryMediums(EMAIL)
    .withUsername(email)
    .withUserPoolId("user pool id")
    .withUserAttributes(
        listOfNotNull(
            AttributeType().withName(CognitoFieldName.EMAIL.fieldName).withValue(email),
            AttributeType().withName(CognitoFieldName.MERCHANT_ID.fieldName)
                .withValue(merchantId.toString()),
            AttributeType().withName(CognitoFieldName.CUSTOMER_ID.fieldName)
                .withValue(customerId.toString())
        )
    )

fun aCreateUserResponse(): AdminCreateUserResult = AdminCreateUserResult()
    .withUser(
        com.amazonaws.services.cognitoidp.model.UserType()
            .withUsername(email)
            .withEnabled(true)
            .withUserStatus("CONFIRMED")
            .withUserCreateDate(createDate)
            .withUserLastModifiedDate(updateDate)
            .withAttributes(
                listOfNotNull(
                    AttributeType().withName(CognitoFieldName.MERCHANT_ID.fieldName)
                        .withValue(merchantId.toString()),
                    AttributeType().withName(CognitoFieldName.CUSTOMER_ID.fieldName)
                        .withValue(customerId.toString())
                )
            )
    )

fun aGroupAssignation() =
    GroupAssignation(
        user = aUser(),
        group = GroupAssignation.Group(
            name = "Payment::Create"
        )
    )

fun aAdminAddUserToGroupRequest() =
    AdminAddUserToGroupRequest()
        .withGroupName("Payment::Create")
        .withUserPoolId("us-east-1_PWeF8HOR0")
        .withUsername(email)

fun aListUsersRequest() =
    ListUsersRequest()
        .withUserPoolId("us-east-1_PWeF8HOR0")

fun aListUsersResponse() =
    ListUsersResult()

fun aListUsersFilterByQuery() = ListUsersFilterByQuery(
    type = MERCHANT,
    email = null,
    customerId = null,
    merchantId = null,
    search = ListUsersFilterByQuery.Search(
        limit = null,
        next = null
    )
)

fun aListUsersFilterByQueryResult() = ListUsersFilterByQueryResult(
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

fun aListUserPage() = ListUserPage(
    users = listOf(aUser()),
    next = "token",
    limit = 10
)

fun aListGroupByUserQuery() =
    ListGroupByUserQuery(
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

fun aAdminListGroupsForUserResult(): AdminListGroupsForUserResult = AdminListGroupsForUserResult()
    .withNextToken("token")
    .withGroups(
        GroupType()
            .withGroupName("name")
            .withDescription("description")
            .withCreationDate(createDate)
            .withLastModifiedDate(updateDate)

    )

fun aListGroupsByUserQueryResult() = ListGroupsByUserQueryResult(
    groups = listOf(
        Group(
            name = "name",
            description = "description",
            audit = Group.Audit(
                creationDate = createDate,
                updateDate = updateDate
            )
        )
    ),
    next = null
)

fun aUserWithGroups() = UserWithGroups(
    user = aUser(),
    groups = listOf(
        Group(
            name = "name",
            description = "description",
            audit = Group.Audit(
                creationDate = createDate,
                updateDate = updateDate
            )
        )
    ),
    next = null,
    limit = null
)

fun aDeleteUserRequest(): AdminDisableUserRequest =
    AdminDisableUserRequest()
        .withUserPoolId("user pool id")
        .withUsername("a username")

fun aDeleteUserResponse(): AdminDisableUserResult = AdminDisableUserResult()

fun aAdminSetUserPasswordRequest(): AdminSetUserPasswordRequest =
    AdminSetUserPasswordRequest()
        .withUsername(email)
        .withUserPoolId("a user pool id")
        .withPassword(password)
        .withPermanent(false)

fun aAdminSetUserPasswordResponse(): AdminSetUserPasswordResult = AdminSetUserPasswordResult()

fun aForgotPasswordUserRequest(): ForgotPasswordRequest =
    ForgotPasswordRequest()
        .withUsername("a username")
        .withClientId("a client id")

fun aForgotPasswordUserResponse(): ForgotPasswordResult = ForgotPasswordResult()

fun aConfirmForgotPasswordRequest(): ConfirmForgotPasswordRequest =
    ConfirmForgotPasswordRequest()
        .withUsername(email)
        .withClientId("a client id")
        .withConfirmationCode("code")
        .withPassword("new password")

fun aConfirmForgotPasswordResponse(): ConfirmForgotPasswordResult = ConfirmForgotPasswordResult()

fun aConfirmForgotPasswordUser() = ConfirmForgotPasswordUser(
    email = email,
    type = MERCHANT,
    password = "new password",
    code = "code"
)

val datetime: OffsetDateTime =
    OffsetDateTime.of(LocalDateTime.of(2022, Month.JANUARY, 19, 11, 23, 23), ZoneOffset.of("-0300"))

val exceptionToApplicationErrorTable =
    table(
        headers("exception", "applicationError"),
        InvalidParameterException("an invalid parameter exception").asRow { unauthorizedUser(it) },
        NotAuthorizedException("a not authorized exception").asRow { unauthorizedUser(it) },
        UserNotFoundException("a user not found exception").let { row(it, userNotFound(email, it)) },
        ResourceNotFoundException("a resource not found exception").let { row(it, resourceNotFound(it)) },
        InternalErrorException("a internal error exception").asRow { unhandledException(it) },
        TooManyRequestsException("a too many request exception").asRow { unhandledException(it) },
    )

private fun Throwable.asRow(applicationErrorProvider: (Throwable) -> ApplicationError) =
    row(this, applicationErrorProvider(this))
