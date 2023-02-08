package com.menta.libs.security.ownership.owner.validator

import com.menta.libs.security.ownership.owner.EntityOwnershipArgumentSource.PATH_VARIABLE
import com.menta.libs.security.ownership.owner.Owner
import com.menta.libs.security.requesterUser.model.RequesterUser.UserType.CUSTOMER
import com.menta.libs.security.requesterUser.model.RequesterUser.UserType.MERCHANT
import com.menta.libs.security.requesterUser.model.RequesterUser.UserType.SUPPORT
import com.menta.libs.security.requesterUser.provider.aRequesterUser
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe

class RequesterUserIsOwnerValidatorSpec : FeatureSpec({

    feature("validate requester user is owner") {

        scenario("requester is owner") {
            RequesterUserIsOwnerValidator().validate(
                listOf(
                    Owner(MERCHANT, PATH_VARIABLE, "merchantId"),
                    Owner(CUSTOMER, PATH_VARIABLE, "customerId")
                ),
                aRequesterUser().copy(type = MERCHANT)
            ) shouldBe Owner(MERCHANT, PATH_VARIABLE, "merchantId")
        }

        scenario("requester is not owner") {
            shouldThrow<RequesterUserIsNotOwner> {
                RequesterUserIsOwnerValidator().validate(
                    listOf(
                        Owner(MERCHANT, PATH_VARIABLE, "merchantId"),
                        Owner(CUSTOMER, PATH_VARIABLE, "customerId")
                    ),
                    aRequesterUser().copy(type = SUPPORT)
                )
            }
        }

    }
})