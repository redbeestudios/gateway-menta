package com.menta.apiacquirers.adapter.`in`

import arrow.core.Either
import arrow.core.flatMap
import com.menta.apiacquirers.adapter.`in`.model.mapper.ToOperableResponseMapper
import com.menta.apiacquirers.application.port.`in`.FindAcquirerCustomerPortIn
import com.menta.apiacquirers.application.port.`in`.FindCustomerPortIn
import com.menta.apiacquirers.domain.AcquirerCustomer
import com.menta.apiacquirers.domain.Customer
import com.menta.apiacquirers.shared.error.model.ApplicationError
import com.menta.apiacquirers.shared.util.log.CompanionLogger
import com.menta.apiacquirers.shared.util.log.benchmark
import com.menta.apiacquirers.shared.util.rest.throwIfLeft
import org.springframework.http.HttpStatus.OK
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/public/acquirers")
class OperableController(
    private val findCustomer: FindCustomerPortIn,
    private val findAcquirerCustomerPortIn: FindAcquirerCustomerPortIn,
    private val toOperableResponseMapper: ToOperableResponseMapper
) {

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/operable")
    @ResponseStatus(OK)
    fun get(@RequestParam(required = true) customerId: UUID) =
        log.benchmark("find acquirer operable by customerId $customerId") {
            doFindCustomerBy(customerId)
                .doFindAcquirerCustomer()
                .throwIfLeft()
                .toResponse()
        }

    private fun doFindCustomerBy(customerId: UUID) =
        findCustomer.execute(customerId)
            .logRight { info("find customer by id $customerId: {}", it) }

    private fun Either<ApplicationError, Customer>.doFindAcquirerCustomer() =
        this.flatMap {
            findAcquirerCustomerPortIn.execute(it.id, it.country)
                .logRight { info("find acquirer customer : {}", it) }
        }

    private fun AcquirerCustomer.toResponse() =
        toOperableResponseMapper.mapFrom(this)
            .log { info("response mapped: {}", it) }

    companion object : CompanionLogger()
}
