package com.kiwi.api.reverse.hexagonal.application.usecase

import com.kiwi.api.reverse.hexagonal.application.STATUSAPPROVE
import com.kiwi.api.reverse.hexagonal.application.aReimbursementRequest
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.date.shouldHaveSameDayAs
import io.kotest.matchers.date.shouldHaveSameMonthAs
import io.kotest.matchers.date.shouldHaveSameYearAs
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldMatch
import java.time.LocalDateTime

class AuthorizeAnnulmentUseCaseSpec : FeatureSpec({

    feature("authorize use case") {

        val useCase = AuthorizeAnnulmentUseCase()

        scenario("authorize annulment") {

            //given an annulment
            val aReimbursement = aReimbursementRequest()
            val date = LocalDateTime.now()

            //when authorizing an annulment
            val response = useCase.authorize(aReimbursement)

            //then auth matches response
            response.authorizationCode shouldMatch "[0-9a-fA-F]{8}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{12}"
            response.transmissionTimestamp shouldHaveSameYearAs date
            response.transmissionTimestamp shouldHaveSameDayAs date
            response.transmissionTimestamp shouldHaveSameMonthAs date
            response.status.code shouldBe STATUSAPPROVE

        }
    }
})
