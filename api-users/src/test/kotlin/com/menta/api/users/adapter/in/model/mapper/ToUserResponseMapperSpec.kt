package com.menta.api.users.adapter.`in`.model.mapper

import com.menta.api.users.adapter.`in`.model.UserResponse
import com.menta.api.users.domain.User
import com.menta.api.users.domain.UserStatus
import com.menta.api.users.domain.UserType.CUSTOMER
import com.menta.api.users.domain.UserType.MERCHANT
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import java.time.Instant
import java.util.Date

class ToUserResponseMapperSpec : FeatureSpec({

    val mapper = ToUserResponseMapper()

    feature("map userResponse from user") {
        val createDate = Date.from(Instant.ofEpochSecond(1234))
        val updateDate = Date.from(Instant.ofEpochSecond(5667))

        scenario("customer user") {
            mapper.mapFrom(
                User(
                    attributes = User.Attributes(
                        email = "user@user.com",
                        merchantId = "a merchantId",
                        customerId = "a customerId",
                        type = MERCHANT
                    ),
                    enabled = true,
                    status = UserStatus.CONFIRMED,
                    audit = User.Audit(
                        creationDate = createDate,
                        updateDate = updateDate
                    )
                )
            ) shouldBe UserResponse(
                attributes = UserResponse.Attributes(
                    email = "user@user.com",
                    merchantId = "a merchantId",
                    customerId = "a customerId",
                    type = MERCHANT
                ),
                status = UserStatus.CONFIRMED,
                audit = UserResponse.Audit(
                    creationDate = createDate,
                    updateDate = updateDate
                )
            )
        }
        scenario("customer user without merchantId") {
            mapper.mapFrom(
                User(
                    attributes = User.Attributes(
                        email = "user@user.com",
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
            ) shouldBe UserResponse(
                attributes = UserResponse.Attributes(
                    email = "user@user.com",
                    merchantId = null,
                    customerId = "a customerId",
                    type = CUSTOMER
                ),
                status = UserStatus.CONFIRMED,
                audit = UserResponse.Audit(
                    creationDate = createDate,
                    updateDate = updateDate
                )
            )
        }
    }
})
