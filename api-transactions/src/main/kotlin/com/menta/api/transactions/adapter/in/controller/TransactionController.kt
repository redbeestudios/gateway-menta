package com.menta.api.transactions.adapter.`in`.controller

import arrow.core.Either
import com.menta.api.transactions.adapter.`in`.model.TransactionResponse
import com.menta.api.transactions.adapter.`in`.model.mapper.ToTransactionResponseMapper
import com.menta.api.transactions.application.port.`in`.FindTransactionByFilterInPort
import com.menta.api.transactions.domain.OperationType
import com.menta.api.transactions.domain.StatusCode
import com.menta.api.transactions.domain.Transaction
import com.menta.api.transactions.domain.TransactionType
import com.menta.api.transactions.shared.error.model.ApplicationError
import com.menta.api.transactions.shared.error.providers.ErrorResponseProvider
import com.menta.api.transactions.shared.util.evaluate
import com.menta.api.transactions.shared.util.log.CompanionLogger
import com.menta.api.transactions.shared.util.log.benchmark
import com.menta.libs.security.ownership.annotation.EntityOwnershipValidation
import com.menta.libs.security.ownership.annotation.EntityOwnershipValidations
import com.menta.libs.security.ownership.owner.EntityOwnershipArgumentSource.QUERY_PARAMETER
import com.menta.libs.security.requesterUser.model.RequesterUser.UserType.CUSTOMER
import com.menta.libs.security.requesterUser.model.RequesterUser.UserType.MERCHANT
import com.menta.libs.security.requesterUser.model.RequesterUser.UserType.SUPPORT
import java.time.ZoneOffset
import java.util.Date
import java.util.UUID
import org.springframework.data.domain.Page
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/public/transactions")
class TransactionController(
    private val findTransactionByFilter: FindTransactionByFilterInPort,
    private val errorResponseProvider: ErrorResponseProvider,
    private val toTransactionResponseMapper: ToTransactionResponseMapper
) {


    @EntityOwnershipValidations(
        [
            EntityOwnershipValidation(MERCHANT, "merchantId", QUERY_PARAMETER),
            EntityOwnershipValidation(CUSTOMER, "customerId", QUERY_PARAMETER),
            EntityOwnershipValidation(SUPPORT, "", QUERY_PARAMETER)
        ]
    )
    @PreAuthorize("hasAuthority('Transaction::Read')")
    @GetMapping
    fun getBy(
        @RequestParam(required = false) operationType: OperationType?,
        @RequestParam(required = false) transactionType: TransactionType?,
        @RequestParam(required = false) transactionId: UUID?,
        @RequestParam(required = false) merchantId: UUID?,
        @RequestParam(required = false) customerId: UUID?,
        @RequestParam(required = false) terminalId: UUID?,
        @RequestParam(name = "status", required = false) statusList: List<StatusCode>?,
        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM") start: Date?,
        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM") end: Date?,
        @RequestParam page: Int,
        @RequestParam size: Int
    ) =
        log.benchmark("find transactions") {
            findTransactionByFilter
                .execute(
                    operationType, transactionType, transactionId, merchantId, customerId, terminalId,
                    start?.toOffsetDateTime(),
                    end?.toOffsetDateTime(),
                    page,
                    size,
                    statusList,
                )
                .map {
                    it.toModel()
                }
                .asResponseEntity()
        }

    private fun Page<Transaction>.toModel() =
        this.map {
            toTransactionResponseMapper.mapFrom(it)
        }

    private fun Date.toOffsetDateTime() = this.toInstant()?.atOffset(ZoneOffset.UTC)

    private fun Either<ApplicationError, Page<TransactionResponse>>.asResponseEntity() =
        evaluate(rightStatusCode = HttpStatus.OK) { errorResponseProvider.provideFor(this) }
            .log { info("response entity: {}", it) }

    companion object : CompanionLogger()
}
