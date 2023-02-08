package com.menta.api.terminals.adapter.out

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.menta.api.terminals.applications.port.out.TerminalRepositoryOutPort
import com.menta.api.terminals.domain.Status
import com.menta.api.terminals.domain.Terminal
import com.menta.api.terminals.shared.error.model.ApplicationError
import com.menta.api.terminals.shared.error.model.ApplicationError.Companion.serverError
import com.menta.api.terminals.shared.utils.logs.CompanionLogger
import com.menta.api.terminals.shared.utils.logs.benchmark
import com.menta.api.terminals.shared.utils.logs.exception
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Component
import java.util.Optional
import java.util.UUID

@Component
class TerminalRepository(
    private val terminalDBRepository: TerminalDBRepository,
    private val mongoTemplate: MongoTemplate
) : TerminalRepositoryOutPort {

    override fun findBy(id: UUID): Optional<Terminal> =
        log.benchmark("find terminal by id") {
            terminalDBRepository.findByIdAndDeleteDateIsNull(id)
        }.log { info("terminal found: {}", it) }

    override fun findBy(serialCode: String, tradeMark: String, model: String): Optional<Terminal> =
        log.benchmark("find terminal by serial code, trade mark and model") {
            terminalDBRepository.findBySerialCodeAndTradeMarkAndModelAndDeleteDateIsNull(serialCode, tradeMark, model)
        }.log { info("terminal found: {}", it) }

    override fun findBySerialCode(serialCode: String) =
        log.benchmark("find terminal by serial code") {
            terminalDBRepository.findBySerialCode(serialCode)
        }.log { info("terminal found: {}", it) }

    override fun create(terminal: Terminal) =
        log.benchmark("create terminal") {
            terminalDBRepository.insert(terminal)
        }.log { info("terminal created: {}", it) }

    override fun update(terminal: Terminal): Either<ApplicationError, Terminal> =
        try {
            log.benchmark("update terminal") {
                terminalDBRepository.save(terminal).right()
            }.log { info("terminal updated: {}", it) }
        } catch (ex: Exception) {
            serverError(ex).left()
                .log { exception(ex) }
        }

    override fun findBy(
        serialCode: String?,
        merchantId: UUID?,
        customerId: UUID?,
        terminalId: UUID?,
        status: Status?,
        pageable: Pageable
    ): Page<Terminal> =
        log.benchmark("Find operation entity by filter") {
            getCriteria(serialCode, merchantId, customerId, terminalId, status)
                .toQuery()
                .getPage(pageable, Terminal::class.java)
        }

    private fun getCriteria(
        serialCode: String?,
        merchantId: UUID?,
        customerId: UUID?,
        terminalId: UUID?,
        status: Status?
    ) =
        Criteria()
            .apply { serialCode?.let { and("serialCode").`is`(it) } }
            .apply { merchantId?.let { and("merchantId").`is`(it) } }
            .apply { customerId?.let { and("customerId").`is`(it) } }
            .apply { status?.let { and("status").`is`(it.name) } }
            .apply { terminalId?.let { and("_id").`is`(it) } }

    private fun Criteria.toQuery() = Query(this)

    private fun <T> Query.getPage(pageable: Pageable, entityClass: Class<T>): Page<T> = let { query ->
        (mongoTemplate.count(query, "terminals")
                to
                query
                    .apply { limit(pageable.pageSize) }
                    .let { mongoTemplate.find(query, entityClass) }
                )
            .let { result ->
                PageImpl(result.second, pageable, result.first)
            }
    }

    companion object : CompanionLogger()
}
