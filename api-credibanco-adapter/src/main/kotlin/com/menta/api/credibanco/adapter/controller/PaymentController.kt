package com.menta.api.credibanco.adapter.controller

import com.menta.api.credibanco.adapter.controller.mapper.ToOperationMapper
import com.menta.api.credibanco.adapter.controller.mapper.ToOperationResponseMapper
import com.menta.api.credibanco.adapter.controller.model.OperationRequest
import com.menta.api.credibanco.application.port.`in`.CreateOperationPortIn
import com.menta.api.credibanco.application.port.`in`.FindCredibancoMerchantPortIn
import com.menta.api.credibanco.application.port.`in`.FindCredibancoTerminalPortIn
import com.menta.api.credibanco.application.port.`in`.ValidateTerminalPortIn
import com.menta.api.credibanco.domain.OperationType.PURCHASE
import com.menta.api.credibanco.shared.error.providers.ErrorResponseProvider
import com.menta.api.credibanco.shared.util.log.CompanionLogger
import com.menta.api.credibanco.shared.util.log.benchmark
import org.springframework.http.HttpStatus.CREATED
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping("/private/payments")
class PaymentController(
    toOperationMapper: ToOperationMapper,
    createOperation: CreateOperationPortIn,
    toOperationResponseMapper: ToOperationResponseMapper,
    errorResponseProvider: ErrorResponseProvider,
    findCredibancoTerminalPortIn: FindCredibancoTerminalPortIn,
    findCredibancoMerchantPortIn: FindCredibancoMerchantPortIn,
    validateTerminal: ValidateTerminalPortIn
) : AbstractController(
    toOperationMapper,
    createOperation,
    toOperationResponseMapper,
    errorResponseProvider,
    findCredibancoTerminalPortIn,
    findCredibancoMerchantPortIn,
    validateTerminal
) {
    @PostMapping
    @ResponseStatus(CREATED)
    fun create(@RequestBody @Valid request: OperationRequest) =
        log.benchmark("create payment") {
            request.executeOperation(PURCHASE)
        }

    companion object : CompanionLogger()
}
