package com.menta.api.terminals.adapter.`in`.controller

import arrow.core.Either
import arrow.core.flatMap
import com.menta.api.terminals.adapter.`in`.model.hateos.TerminalModel
import com.menta.api.terminals.adapter.`in`.model.TerminalRequest
import com.menta.api.terminals.adapter.`in`.model.TerminalResponse
import com.menta.api.terminals.adapter.`in`.model.mapper.ToPreTerminalMapper
import com.menta.api.terminals.adapter.`in`.model.mapper.ToPagedTerminalMapper
import com.menta.api.terminals.adapter.`in`.model.mapper.ToTerminalResponseMapper
import com.menta.api.terminals.applications.port.`in`.CreateTerminalPortIn
import com.menta.api.terminals.applications.port.`in`.DeleteTerminalPortIn
import com.menta.api.terminals.applications.port.`in`.FindTerminalByFilterPortIn
import com.menta.api.terminals.applications.port.`in`.FindTerminalPortIn
import com.menta.api.terminals.domain.PreTerminal
import com.menta.api.terminals.domain.Status
import com.menta.api.terminals.domain.Terminal
import com.menta.api.terminals.shared.error.model.ApplicationError
import com.menta.api.terminals.shared.error.providers.ErrorResponseProvider
import com.menta.api.terminals.shared.utils.evaluate
import com.menta.api.terminals.shared.utils.logs.CompanionLogger
import com.menta.api.terminals.shared.utils.logs.benchmark
import org.springframework.data.domain.Page
import org.springframework.hateoas.PagedModel
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.HttpStatus.OK
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/private/terminals")
class TerminalController(
    private val findTerminal: FindTerminalPortIn,
    private val findTerminalByFilter: FindTerminalByFilterPortIn,
    private val createTerminalPortIn: CreateTerminalPortIn,
    private val deleteTerminalPortIn: DeleteTerminalPortIn,
    private val toResponseMapper: ToTerminalResponseMapper,
    private val toPreTerminalMapper: ToPreTerminalMapper,
    private val errorResponseProvider: ErrorResponseProvider,
    private val toPagedTerminalMapper: ToPagedTerminalMapper
) {

    @GetMapping("/{id}")
    fun getBy(@PathVariable id: UUID) =
        log.benchmark("find terminal: $id") {
            findTerminal.execute(id)
                .map { it.toResponse() }
                .asResponseEntity()
        }

    @GetMapping
    fun getBy(
        @RequestParam(required = false) serialCode: String?,
        @RequestParam(required = false) merchantId: UUID?,
        @RequestParam(required = false) customerId: UUID?,
        @RequestParam(required = false) terminalId: UUID?,
        @RequestParam(required = false) status: Status?,
        @RequestParam(required = false, defaultValue = "0") page: Int,
        @RequestParam(required = false, defaultValue = "10") size: Int
    ) =
        log.benchmark("find terminals") {
            findTerminalByFilter.execute(
                serialCode, merchantId, customerId, terminalId, status,
                page, size
            ).map {
               it.toResponse()
            }
                .asResponseEntity()
        }

    private fun Either<ApplicationError, PagedModel<TerminalModel>>.asResponseEntity() =
        evaluate(rightStatusCode = OK) { errorResponseProvider.provideFor(this) }
            .log { info("response entity: {}", it) }

    @PostMapping
    fun create(@RequestBody terminalRequest: TerminalRequest) =
        log.benchmark("Create Terminal") {
            terminalRequest
                .toDomain()
                .execute()
                .map { it.toResponse() }
                .asResponseEntity(CREATED)
        }

    @DeleteMapping("/{terminalId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable terminalId: UUID) =
        log.benchmark("Delete terminal by id $terminalId") {
            findTerminal.execute(terminalId)
                .flatMap {
                    deleteTerminalPortIn.execute(it)
                }.asResponseNotContent()
        }

    private fun Page<Terminal>.toResponse() =
        toPagedTerminalMapper.map(this)

    private fun Terminal.toResponse() =
        toResponseMapper.mapFrom(this)
            .log { debug("response: {}", it) }

    private fun Either<ApplicationError, TerminalResponse>.asResponseEntity(rightStatusCode: HttpStatus = OK) =
        evaluate(rightStatusCode = rightStatusCode) { errorResponseProvider.provideFor(this) }
            .log { debug("response entity: {}", it) }

    private fun Either<ApplicationError, Terminal>.asResponseNotContent() =
        evaluate(rightStatusCode = HttpStatus.NO_CONTENT) { errorResponseProvider.provideFor(this) }

    private fun TerminalRequest.toDomain() =
        toPreTerminalMapper.map(this)
            .log { debug("Created PreTerminal: {}", it) }

    private fun PreTerminal.execute() =
        createTerminalPortIn.execute(this, findTerminal.findByUnivocity(serialCode, tradeMark, model))

    companion object : CompanionLogger()
}
