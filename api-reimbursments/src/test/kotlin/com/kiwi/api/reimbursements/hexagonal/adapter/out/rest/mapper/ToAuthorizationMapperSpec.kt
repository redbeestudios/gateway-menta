package com.kiwi.api.reimbursements.hexagonal.adapter.out.rest.mapper

import com.kiwi.api.reimbursements.hexagonal.application.anAcquirerResponse
import com.kiwi.api.reimbursements.hexagonal.application.anAuthorization
import com.kiwi.api.reimbursements.hexagonal.domain.Authorization
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks

class ToAuthorizationMapperSpec : FeatureSpec({

    feature("map created refund") {

        val mapper = ToAuthorizationMapper()
        beforeEach { clearAllMocks() }

        scenario("successful mapping") {
            val authorization = anAuthorization()
            val acquirerResponse = anAcquirerResponse()

            mapper.map(acquirerResponse) shouldBe with(authorization) {
                Authorization(
                    authorizationCode = authorizationCode,
                    retrievalReferenceNumber = retrievalReferenceNumber,
                    displayMessage = displayMessage?.let {
                        Authorization.DisplayMessage(
                            message = it.message,
                            useCode = it.useCode
                        )
                    },
                    status = Authorization.Status(
                        code = status.code,
                        situation = status.situation?.let {
                            Authorization.Status.Situation(
                                id = it.id,
                                description = it.description
                            )
                        }
                    )
                )
            }
        }
    }
})
