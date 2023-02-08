package com.menta.api.users.domain.mapper

import com.amazonaws.services.cognitoidp.model.AttributeType
import com.amazonaws.services.cognitoidp.model.ListUsersResult
import com.amazonaws.services.cognitoidp.model.UserStatusType.CONFIRMED
import com.menta.api.users.domain.ListUsersFilterByQueryResult
import com.menta.api.users.domain.User
import com.menta.api.users.domain.UserStatus
import com.menta.api.users.domain.UserType
import com.menta.api.users.domain.UserType.CUSTOMER
import com.menta.api.users.email
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import java.time.Instant
import java.util.Date

class ToListUsersFilterByQueryResultMapperSpec : FeatureSpec({

    val userMapper = mockk<ToUserMapper>()
    val mapper = ToListUsersFilterByQueryResultMapper(userMapper)

    beforeEach { clearAllMocks() }

    feature("map user from cognito get list user response") {
        val createDate = Date.from(Instant.ofEpochSecond(1234))
        val updateDate = Date.from(Instant.ofEpochSecond(5667))

        scenario("map support") {
            val type = UserType.SUPPORT
            val response = ListUsersResult()
                .withUsers(
                    com.amazonaws.services.cognitoidp.model.UserType()
                        .withUsername(email)
                        .withEnabled(true)
                        .withUserStatus(CONFIRMED)
                        .withUserCreateDate(createDate)
                        .withUserLastModifiedDate(updateDate)
                )
                .withPaginationToken("token")

            val user = User(
                attributes = User.Attributes(
                    email = email,
                    merchantId = null,
                    customerId = null,
                    type = UserType.SUPPORT
                ),
                enabled = true,
                status = UserStatus.CONFIRMED,
                audit = User.Audit(
                    creationDate = createDate,
                    updateDate = updateDate
                )
            )

            every { userMapper.mapFrom(response.users[0], type) } returns user

            mapper.mapFrom(response, type) shouldBe
                ListUsersFilterByQueryResult(
                    users = listOf(
                        user
                    ),
                    next = "token"
                )
        }
        scenario("map customer") {
            val type = CUSTOMER
            val response = ListUsersResult()
                .withUsers(
                    com.amazonaws.services.cognitoidp.model.UserType()
                        .withUsername(email)
                        .withEnabled(true)
                        .withUserStatus(CONFIRMED)
                        .withUserCreateDate(createDate)
                        .withUserLastModifiedDate(updateDate)
                        .withAttributes(
                            listOf(
                                AttributeType().withName("family_name").withValue("a customerId")
                            )
                        )
                )
                .withPaginationToken("token")

            val user = User(
                attributes = User.Attributes(
                    email = email,
                    merchantId = null,
                    customerId = "a customerId",
                    type = CUSTOMER
                ),
                enabled = true,
                status = UserStatus.CONFIRMED,
                audit = User.Audit(
                    creationDate = createDate,
                    updateDate = updateDate
                )
            )

            every { userMapper.mapFrom(response.users[0], type) } returns user

            mapper.mapFrom(response, type) shouldBe
                ListUsersFilterByQueryResult(
                    users = listOf(user),
                    next = "token"
                )
        }
        scenario("map merchant") {
            val type = UserType.MERCHANT
            val response = ListUsersResult()
                .withUsers(
                    com.amazonaws.services.cognitoidp.model.UserType()
                        .withUsername(email)
                        .withEnabled(true)
                        .withUserStatus(CONFIRMED)
                        .withUserCreateDate(createDate)
                        .withUserLastModifiedDate(updateDate)
                        .withAttributes(
                            listOf(
                                AttributeType().withName("given_name").withValue("a merchantId"),
                                AttributeType().withName("family_name").withValue("a customerId")
                            )
                        )
                )
                .withPaginationToken("token")

            val user = User(
                attributes = User.Attributes(
                    email = email,
                    merchantId = "a merchantId",
                    customerId = "a customerId",
                    type = UserType.MERCHANT
                ),
                enabled = true,
                status = UserStatus.CONFIRMED,
                audit = User.Audit(
                    creationDate = createDate,
                    updateDate = updateDate
                )
            )

            every { userMapper.mapFrom(response.users[0], type) } returns user

            mapper.mapFrom(response, type) shouldBe
                ListUsersFilterByQueryResult(
                    users = listOf(user),
                    next = "token"
                )
        }
    }
})
