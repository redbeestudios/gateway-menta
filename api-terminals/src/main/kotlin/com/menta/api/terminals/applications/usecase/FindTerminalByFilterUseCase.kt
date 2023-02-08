package com.menta.api.terminals.applications.usecase

import arrow.core.Either
import com.menta.api.terminals.applications.port.`in`.FindTerminalByFilterPortIn
import com.menta.api.terminals.applications.port.out.TerminalRepositoryOutPort
import com.menta.api.terminals.domain.Status
import com.menta.api.terminals.domain.Terminal
import com.menta.api.terminals.shared.error.model.ApplicationError
import com.menta.api.terminals.shared.error.model.ApplicationError.Companion.terminalNotFound
import com.menta.api.terminals.shared.utils.either.rightIfPresent
import com.menta.api.terminals.shared.utils.logs.CompanionLogger
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class FindTerminalByFilterUseCase(
    private val terminalRepository: TerminalRepositoryOutPort
) : FindTerminalByFilterPortIn {
    override fun execute(
        serialCode: String?,
        merchantId: UUID?,
        customerId: UUID?,
        terminalId: UUID?,
        status: Status?,
        page: Int,
        size: Int
    ): Either<ApplicationError, Page<Terminal>> =
        findBy(
            serialCode,
            merchantId,
            customerId,
            terminalId,
            status,
            pageable(page, size)
        ).shouldBePresent(serialCode, merchantId, customerId, terminalId, status)

    private fun findBy(
        serialCode: String?,
        merchantId: UUID?,
        customerId: UUID?,
        terminalId: UUID?,
        status: Status?,
        pageable: Pageable
    ) =
        terminalRepository.findBy(
            serialCode,
            merchantId,
            customerId,
            terminalId,
            status,
            pageable
        ).log { info("terminal searched") }

    private fun Page<Terminal>.shouldBePresent(
        serialCode: String?,
        merchantId: UUID?,
        customerId: UUID?,
        terminalId: UUID?,
        status: Status?
    ) =
        rightIfPresent(
            error = terminalNotFound(
                serialCode,
                merchantId,
                customerId,
                terminalId,
                status
            )
        )
            .logEither(
                {
                    error(
                        "terminal with serialCode: {},  merchantId {}, customerId {}, terminalId {}, status {}  not found",
                        serialCode,
                        merchantId,
                        customerId,
                        terminalId,
                        status
                    )
                },
                {
                    info(
                        "terminal with serialCode: {},  merchantId {}, customerId {}, terminalId {}, status {}  not found",
                        serialCode,
                        merchantId,
                        customerId,
                        terminalId,
                        status
                    )
                }
            )

    private fun pageable(page: Int, size: Int): Pageable =
        PageRequest.of(page, size)

    companion object : CompanionLogger()
}
