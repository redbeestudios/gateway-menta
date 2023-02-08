package com.menta.api.users.domain.mapper

import com.menta.api.users.adapter.`in`.model.CreateUserRequest
import com.menta.api.users.domain.NewUser
import com.menta.api.users.domain.UserType
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import java.util.UUID

class ToNewUserMapperSpec : FeatureSpec({

    val mapper = ToNewUserMapper()

    feature("map new user from create user request") {
        val customerId = UUID.fromString("0f14d0ab-9605-4a62-a9e4-5ed26688389b")
        val merchantId = UUID.fromString("669ed2f9-4c04-4c85-8b48-1a8fd67de963")

        scenario("user with merchantId") {
            mapper.mapFrom(
                CreateUserRequest(
                    userType = UserType.MERCHANT,
                    attributes = CreateUserRequest.Attributes(
                        email = "email@menta.com",
                        customerId = customerId,
                        merchantId = merchantId
                    )
                )
            ) shouldBe NewUser(
                type = UserType.MERCHANT,
                attributes = NewUser.Attributes(
                    email = "email@menta.com",
                    customerId = customerId,
                    merchantId = merchantId
                )
            )
        }
        scenario("user without merchantId") {
            mapper.mapFrom(
                CreateUserRequest(
                    userType = UserType.MERCHANT,
                    attributes = CreateUserRequest.Attributes(
                        email = "email@menta.com",
                        customerId = customerId,
                        merchantId = null
                    )
                )
            ) shouldBe NewUser(
                type = UserType.MERCHANT,
                attributes = NewUser.Attributes(
                    email = "email@menta.com",
                    customerId = customerId,
                    merchantId = null
                )
            )
        }
    }
})
