package com.kiwi.api.reverse.hexagonal.application.usecase

import com.kiwi.api.reverse.hexagonal.application.aBatchCloseRequest
import com.kiwi.api.reverse.hexagonal.application.anAuthorization
import com.kiwi.api.reverse.hexagonal.application.currency
import com.kiwi.api.reverse.hexagonal.domain.BatchClose
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe

class BuildBatchCloseUseCaseSpec : FeatureSpec({

    feature("build batch close use case") {

        val useCase = BuildBatchCloseUseCase()

        scenario("build a batch close") {

            //given
            val request = aBatchCloseRequest()
            val authorization = anAuthorization()
            val id = "1234"
            val merchantId = "merchantId"

            //when
            val result = useCase.buildFrom(request, authorization, id)

            //then
            result shouldBe with(request) {
                BatchClose(
                    id = id,
                    authorization = authorization,
                    trace = trace,
                    batch = batch,
                    hostMessage = hostMessage,
                    terminal = BatchClose.Terminal(
                        id = "123",
                        serialCode = terminal.serialCode
                    ),
                    datetime = datetime,
                    merchant = BatchClose.Merchant(
                        id = merchantId
                    ),
                    ticket = ticket,
                    softwareVersion = softwareVersion,
                    total = BatchClose.Total(
                        operationCode = total.operationCode,
                        amount = total.amount,
                        currency = total.currency
                    )
                )
            }
        }
    }
})
