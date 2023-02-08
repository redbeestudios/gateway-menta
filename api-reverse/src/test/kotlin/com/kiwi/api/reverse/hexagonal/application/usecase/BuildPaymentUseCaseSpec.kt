package com.kiwi.api.reverse.hexagonal.application.usecase

import com.kiwi.api.reverse.hexagonal.application.aPaymentRequest
import com.kiwi.api.reverse.hexagonal.application.anAuthorization
import com.kiwi.api.reverse.hexagonal.domain.Card
import com.kiwi.api.reverse.hexagonal.domain.Payment
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe

class BuildPaymentUseCaseSpec : FeatureSpec({

    feature("build payment use case") {

        val useCase = BuildPaymentUseCase()

        scenario("build an payment") {

            //given
            val request = aPaymentRequest()
            val authorization = anAuthorization()
            val id = "1234"

            //when
            val result = useCase.buildFrom(request, authorization, id)

            //then
            result shouldBe with(request) {
                Payment(
                    id = id,
                    paymentId = paymentId,
                    authorization = authorization,
                    capture = Payment.Capture(
                        card = Card(
                            iccData = capture.card.iccData,
                            cardSequenceNumber = capture.card.cardSequenceNumber,
                            bank = capture.card.bank,
                            type = capture.card.type,
                            brand = capture.card.brand,
                            track1 = capture.card.track1,
                            track2 = capture.card.track2,
                            cvv = capture.card.cvv,
                            expirationDate = capture.card.expirationDate,
                            pan = capture.card.pan,
                            holder = Card.Holder(
                                name = capture.card.holder.name,
                                identification = Card.Holder.Identification(
                                    number = capture.card.holder.identification.number,
                                    type = capture.card.holder.identification.type
                                )
                            )
                        ),
                        inputMode = capture.inputMode,
                    ),
                    amount = Payment.Amount(
                        total = amount.total,
                        currency = amount.currency,
                        breakdown = amount.breakdown.map {
                            Payment.Amount.Breakdown(
                                description = it.description,
                                amount = it.amount
                            )
                        }
                    ),
                    installments = installments,
                    trace = trace,
                    batch = batch,
                    hostMessage = hostMessage,
                    ticket = ticket,
                    terminal = Payment.Terminal(
                        id = "123",
                        serialCode = terminal.serialCode
                    ),
                    dateTime = datetime,
                    merchant = Payment.Merchant(
                        id = "merchantId"
                    )
                )
            }
        }
    }
})
