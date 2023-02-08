package com.menta.api.credibanco.adapter.controller

import arrow.core.Either
import arrow.core.flatMap
import com.menta.api.credibanco.adapter.controller.ReimbursementController.Companion.log
import com.menta.api.credibanco.adapter.controller.mapper.ToOperationMapper
import com.menta.api.credibanco.adapter.controller.mapper.ToOperationResponseMapper
import com.menta.api.credibanco.adapter.controller.model.OperationRequest
import com.menta.api.credibanco.application.port.`in`.CreateOperationPortIn
import com.menta.api.credibanco.application.port.`in`.FindCredibancoMerchantPortIn
import com.menta.api.credibanco.application.port.`in`.FindCredibancoTerminalPortIn
import com.menta.api.credibanco.application.port.`in`.ValidateTerminalPortIn
import com.menta.api.credibanco.domain.CreatedOperation
import com.menta.api.credibanco.domain.Operation
import com.menta.api.credibanco.domain.OperationType
import com.menta.api.credibanco.shared.error.model.ApplicationError
import com.menta.api.credibanco.shared.error.providers.ErrorResponseProvider
import com.menta.api.credibanco.shared.util.rest.evaluate
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.springframework.http.HttpStatus.CREATED

abstract class AbstractController(
    private val toOperationMapper: ToOperationMapper,
    private val createOperation: CreateOperationPortIn,
    private val toOperationResponseMapper: ToOperationResponseMapper,
    private val errorResponseProvider: ErrorResponseProvider,
    private val findCredibancoTerminalPortIn: FindCredibancoTerminalPortIn,
    private val findCredibancoMerchantPortIn: FindCredibancoMerchantPortIn,
    private val validateTerminal: ValidateTerminalPortIn

) {
    protected fun OperationRequest.executeOperation(operationType: OperationType) =
        validate()
            .toDomain(operationType)
            .create()// 420 despues del cierre
            .buildResponse(this)
            .evaluate(CREATED) {
                errorResponseProvider.provideFor(this)
            }

    private fun OperationRequest.validate() =
        validateTerminal
            .execute(terminal)
            .map { this }

    private fun Either<ApplicationError, OperationRequest>.toDomain(operationType: OperationType) =
        map {
            runBlocking {
                val credibancoTerminal = async { it.findCredibancoTerminal() }
                val credibancoMerchant = async { it.findCredibancoMerchant() }
                toOperationMapper.map(
                    it,
                    operationType,
                    credibancoTerminal.await(),
                    credibancoMerchant.await()
                )
            }
        }

    private fun Either<ApplicationError, Operation>.create() =
        flatMap { createOperation.execute(it) }

    private fun Either<ApplicationError, CreatedOperation>.buildResponse(request: OperationRequest) =
        map {
            toOperationResponseMapper.map(it, request)
                .log { response -> info("response: {}", response) }
        }

    private fun OperationRequest.findCredibancoTerminal() =
        findCredibancoTerminalPortIn.execute(terminal.id)

    private fun OperationRequest.findCredibancoMerchant() =
        findCredibancoMerchantPortIn.execute(merchant.id)
}
