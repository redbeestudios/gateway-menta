package com.kiwi.api.batchcloses.hexagonal.application.usecase

import com.kiwi.api.batchcloses.hexagonal.application.aBatchCloseRequest
import com.kiwi.api.batchcloses.hexagonal.application.anAuthorization
import com.kiwi.api.batchcloses.hexagonal.application.merchantId
import com.kiwi.api.batchcloses.hexagonal.domain.BatchClose
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

            //when
            val result = useCase.buildFrom(request, authorization, merchantId, id)

            //then
            result shouldBe with(request) {
                BatchClose(
                    id = id,
                    authorization = authorization,
                    merchant = BatchClose.Merchant(
                        id = merchantId
                    ),
                    terminal = BatchClose.Terminal(
                        id = "123",
                        serialCode = terminal.serialCode,
                        softwareVersion = terminal.softwareVersion
                    ),
                    ticket = ticket,
                    trace = trace,
                    batch = batch,
                    hostMessage = hostMessage,
                    datetime = datetime,
                    totals = totals.map {
                        BatchClose.Total(
                            operationCode = it.operationCode,
                            amount = it.amount,
                            currency = it.currency
                        )
                    }
                )
            }
        }
    }
})
