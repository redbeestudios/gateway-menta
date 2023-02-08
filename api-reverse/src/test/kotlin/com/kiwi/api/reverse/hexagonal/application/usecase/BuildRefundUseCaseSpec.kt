package com.kiwi.api.reverse.hexagonal.application.usecase

import com.kiwi.api.reverse.hexagonal.application.aReimbursementRequest
import com.kiwi.api.reverse.hexagonal.application.anAuthorization
import com.kiwi.api.reverse.hexagonal.domain.*
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe

class BuildRefundUseCaseSpec : FeatureSpec({

    feature("build refund use case") {

        val useCase = BuildAnnulmentUseCase()

        scenario("build a refund") {

            //given
            val request = aReimbursementRequest()
            val authorization = anAuthorization()
            val id = "1234"
            val merchantId = "1234"

            //when
            val result = useCase.buildFrom(request, authorization, id, merchantId)

            //then
            result shouldBe with(request) {
                Annulment(
                    id = id,
                    paymentId = paymentId,
                    authorization = authorization,
                    capture = Capture(
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
                            holder =Card.Holder(
                                name = capture.card.holder.name,
                                identification = Card.Holder.Identification(
                                    number = capture.card.holder.identification.number,
                                    type = capture.card.holder.identification.type
                                )
                            ),
                        ),
                        inputMode = capture.inputMode,
                    ),
                    amount = Amount(
                        total = amount.total,
                        currency = amount.currency,
                        breakdown = amount.breakdown.map {
                            Amount.Breakdown(
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
                    terminal = Terminal(
                        id = "id terminal",
                        serialCode = terminal.serialCode
                    ),
                    datetime = datetime,
                    merchant = Merchant(
                        id = merchantId
                    )
                )
            }
        }
    }
})
