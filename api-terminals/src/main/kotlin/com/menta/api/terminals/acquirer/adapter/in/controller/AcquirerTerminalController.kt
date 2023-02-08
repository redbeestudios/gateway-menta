package com.menta.api.terminals.acquirer.adapter.`in`.controller

import arrow.core.Either
import arrow.core.flatMap
import com.menta.api.terminals.acquirer.adapter.`in`.model.AcquirerTerminalRequest
import com.menta.api.terminals.acquirer.adapter.`in`.model.AcquirerTerminalResponse
import com.menta.api.terminals.acquirer.adapter.`in`.model.mapper.ToAcquirerTerminalResponseMapper
import com.menta.api.terminals.acquirer.adapter.`in`.model.mapper.ToPreAcquirerTerminalMapper
import com.menta.api.terminals.acquirer.application.port.`in`.FindAcquirerTerminalPortIn
import com.menta.api.terminals.acquirer.domain.AcquirerTerminal
import com.menta.api.terminals.acquirer.domain.PreAcquirerTerminal
import com.menta.api.terminals.acquirer.domain.provider.AcquirerProvider
import com.menta.api.terminals.applications.port.`in`.CreateAcquirerTerminalPortIn
import com.menta.api.terminals.applications.port.`in`.UpdateAcquirerTerminalPortIn
import com.menta.api.terminals.shared.error.model.ApplicationError
import com.menta.api.terminals.shared.error.providers.ErrorResponseProvider
import com.menta.api.terminals.shared.utils.evaluate
import com.menta.api.terminals.shared.utils.logs.CompanionLogger
import com.menta.api.terminals.shared.utils.logs.benchmark
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.OK
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.util.Locale
import java.util.UUID

@RestController
@RequestMapping("/private/terminals/{id}/acquirers")
class AcquirerTerminalController(
    private val findAcquirerTerminal: FindAcquirerTerminalPortIn,
    private val createAcquirerTerminal: CreateAcquirerTerminalPortIn,
    private val updateAcquirerTerminal: UpdateAcquirerTerminalPortIn,
    private val toResponseMapper: ToAcquirerTerminalResponseMapper,
    private val toPreAcquirerTerminalMapper: ToPreAcquirerTerminalMapper,
    private val errorResponseProvider: ErrorResponseProvider,
    private val acquirerProvider: AcquirerProvider
) {

    @GetMapping("/{acquirer}")
    fun getBy(@PathVariable acquirer: String, @PathVariable id: UUID) =
        log.benchmark("find terminal: $id for acquirer: $acquirer") {
            getAcquirerBy(acquirer.uppercase(Locale.getDefault())).flatMap {
                doGetBy(it.id, id)
            }
                .map { it.toResponse() }
                .asResponseEntity()
        }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@RequestBody acquirerTerminalRequest: AcquirerTerminalRequest, @PathVariable id: UUID) =
        log.benchmark("create acquirer terminal") {
            getAcquirerBy(acquirerTerminalRequest.acquirerId.uppercase(Locale.getDefault())).flatMap {
                    toDomain(acquirerTerminalRequest, id)
                    .create()
            }
                .map { it.toResponse() }
                .asResponseEntity(HttpStatus.CREATED)
        }

    @PutMapping
    @ResponseStatus(OK)
    fun update(@RequestBody acquirerTerminalRequest: AcquirerTerminalRequest, @PathVariable id: UUID) =
        log.benchmark("update acquirer terminal") {
            getAcquirerBy(acquirerTerminalRequest.acquirerId.uppercase(Locale.getDefault())).flatMap {
                    toDomain(acquirerTerminalRequest, id)
                    .update()
            }
                .map { it.toResponse() }
                .asResponseEntity(OK)
        }

    private fun getAcquirerBy(acquirer: String) =
        acquirerProvider.provideFor(acquirer)
            .logRight { info("acquirer found: {}", it) }

    private fun toDomain(acquirerTerminalRequest: AcquirerTerminalRequest, terminalId: UUID) =
        toPreAcquirerTerminalMapper.map(acquirerTerminalRequest, terminalId)
            .log { info("Created acquirer terminal: {}", it) }

    private fun PreAcquirerTerminal.create() =
        createAcquirerTerminal.execute(this, findAcquirerTerminal.find(terminalId, acquirerId))

    private fun PreAcquirerTerminal.update() =
        updateAcquirerTerminal.execute(this, findAcquirerTerminal.find(terminalId, acquirerId))

    private fun doGetBy(acquirer: String, id: UUID) =
        findAcquirerTerminal.execute(acquirer, id)
            .logRight { info("acquirer terminal found: {}", it) }

    private fun AcquirerTerminal.toResponse() =
        toResponseMapper.mapFrom(this)
            .log { info("response: {}", it) }

    private fun Either<ApplicationError, AcquirerTerminalResponse>.asResponseEntity(httpStatus: HttpStatus = OK) =
        evaluate(rightStatusCode = httpStatus) { errorResponseProvider.provideFor(this) }
            .log { info("response entity: {}", it) }

    companion object : CompanionLogger()
}
