package com.kiwi.api.reimbursements.hexagonal.adapter.`in`.controller

import arrow.core.Either
import arrow.core.flatMap
import com.kiwi.api.reimbursements.hexagonal.adapter.`in`.controller.mapper.ToReimbursementDomainMapper
import com.kiwi.api.reimbursements.hexagonal.adapter.`in`.controller.mapper.ToReimbursementResponseMapper
import com.kiwi.api.reimbursements.hexagonal.adapter.`in`.controller.model.ReimbursementRequest
import com.kiwi.api.reimbursements.hexagonal.application.port.`in`.CreateAnnulmentPortIn
import com.kiwi.api.reimbursements.hexagonal.application.port.`in`.CreateRefundPortIn
import com.kiwi.api.reimbursements.hexagonal.application.port.`in`.FindCustomerPortIn
import com.kiwi.api.reimbursements.hexagonal.application.port.`in`.FindMerchantPortIn
import com.kiwi.api.reimbursements.hexagonal.application.port.`in`.FindTerminalPortIn
import com.kiwi.api.reimbursements.hexagonal.application.port.`in`.FindTransactionPortIn
import com.kiwi.api.reimbursements.hexagonal.domain.Annulment
import com.kiwi.api.reimbursements.hexagonal.domain.CreatedAnnulment
import com.kiwi.api.reimbursements.hexagonal.domain.CreatedRefund
import com.kiwi.api.reimbursements.hexagonal.domain.Refund
import com.kiwi.api.reimbursements.hexagonal.domain.Terminal
import com.kiwi.api.reimbursements.hexagonal.domain.Transaction
import com.kiwi.api.reimbursements.shared.error.model.ApplicationError
import com.kiwi.api.reimbursements.shared.error.providers.ErrorResponseProvider
import com.kiwi.api.reimbursements.shared.util.log.CompanionLogger
import com.kiwi.api.reimbursements.shared.util.log.benchmark
import com.kiwi.api.reimbursements.shared.util.rest.evaluate
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.springframework.http.HttpStatus.CREATED
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/public")
class ReimbursementController(
    private val createAnnulmentPortIn: CreateAnnulmentPortIn,
    private val createRefund: CreateRefundPortIn,
    private val findCustomer: FindCustomerPortIn,
    private val findTerminal: FindTerminalPortIn,
    private val findMerchantPortIn: FindMerchantPortIn,
    private val findTransactionPortIn: FindTransactionPortIn,
    private val toReimbursementResponseMapper: ToReimbursementResponseMapper,
    private val toReimbursementDomainMapper: ToReimbursementDomainMapper,
    private val errorResponseProvider: ErrorResponseProvider

) {

    @PostMapping("/annulments")
    @ResponseStatus(CREATED)
    fun createAnnulment(
        @RequestBody request: ReimbursementRequest
    ) = log.benchmark("create annulment") {
        request.paymentId.findTransaction().flatMap {
            toDomainAnnulment(request)
                .createAnnulment().map {
                    it.toResponse()
                }
        }
            .evaluate(CREATED) { errorResponseProvider.provideFor(this) }
    }

    @PostMapping("/refunds")
    @ResponseStatus(CREATED)
    fun createRefund(
        @RequestBody request: ReimbursementRequest
    ) = log.benchmark("create refund") {
        request.paymentId.findTransaction().flatMap {
            toDomainRefund(request)
                .createRefundFrom().map {
                    it.toResponse()
                }
        }.evaluate(CREATED) { errorResponseProvider.provideFor(this) }
    }

    private fun UUID.findTransaction(): Either<ApplicationError, Transaction> =
        findTransactionPortIn.execute(this)

    private fun toDomainRefund(request: ReimbursementRequest) =
        findTerminal(request.terminal.id).let {
            runBlocking {
                val merchant = async { it.findMerchant() }
                val customer = async { it.findCustomer() }

                toReimbursementDomainMapper.mapToRefund(request, it, merchant.await(), customer.await())
                    .log { info("Refund mapped for request: {}", it) }
            }
        }

    private fun toDomainAnnulment(request: ReimbursementRequest) =
        findTerminal(request.terminal.id).let {
            runBlocking {
                val merchant = async { it.findMerchant() }
                val customer = async { it.findCustomer() }

                toReimbursementDomainMapper.mapToAnnulment(request, it, merchant.await(), customer.await())
                    .log { info("Annulment mapped for request: {}", it) }
            }
        }

    private fun findTerminal(terminalId: UUID) =
        findTerminal.execute(terminalId)

    suspend fun Terminal.findCustomer() =
        findCustomer.execute(customerId)

    suspend fun Terminal.findMerchant() =
        findMerchantPortIn.execute(merchantId)

    private fun Annulment.createAnnulment() =
        createAnnulmentPortIn.execute(this)
            .logRight { info("annulment created") }

    private fun Refund.createRefundFrom() =
        createRefund.execute(this)
            .logRight { info("refund created") }

    private fun CreatedAnnulment.toResponse() =
        toReimbursementResponseMapper.map(this)
            .log { info("response mapped for reimbursement: {}", it.id) }

    private fun CreatedRefund.toResponse() =
        toReimbursementResponseMapper.map(this)
            .log { info("response mapped for reimbursement: {}", it.id) }

    companion object : CompanionLogger()
}
