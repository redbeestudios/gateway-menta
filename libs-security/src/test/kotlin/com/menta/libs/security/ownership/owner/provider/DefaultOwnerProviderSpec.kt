package com.menta.libs.security.ownership.owner.provider

import com.menta.libs.security.ownership.annotation.EntityOwnershipValidation
import com.menta.libs.security.ownership.owner.EntityOwnershipArgumentSource.BODY_ATTRIBUTE
import com.menta.libs.security.ownership.owner.EntityOwnershipArgumentSource.PATH_VARIABLE
import com.menta.libs.security.ownership.owner.Owner
import com.menta.libs.security.requesterUser.model.RequesterUser.UserType.CUSTOMER
import com.menta.libs.security.requesterUser.model.RequesterUser.UserType.MERCHANT
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe

class DefaultOwnerProviderSpec : FeatureSpec({

    feature("provide from list of annotation") {

        scenario("empty list") {
            DefaultOwnerProvider().provideFrom(
                emptyList()
            ) shouldBe emptyList()
        }

        scenario("list mapped") {
            DefaultOwnerProvider().provideFrom(
                listOf(
                    EntityOwnershipValidation(CUSTOMER, "a name", PATH_VARIABLE),
                    EntityOwnershipValidation(MERCHANT, "another name", BODY_ATTRIBUTE)
                )
            ) shouldBe listOf(
                Owner(CUSTOMER, PATH_VARIABLE, "a name"),
                Owner(MERCHANT, BODY_ATTRIBUTE, "another name")
            )

        }
    }
})