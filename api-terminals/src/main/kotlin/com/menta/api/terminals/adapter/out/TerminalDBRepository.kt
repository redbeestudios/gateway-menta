package com.menta.api.terminals.adapter.out

import com.menta.api.terminals.domain.Terminal
import java.util.Optional
import java.util.UUID
import org.springframework.data.mongodb.repository.MongoRepository

interface TerminalDBRepository : MongoRepository<Terminal, UUID> {
    fun findBySerialCode(serialCode: String): Optional<Terminal>

    fun findByIdAndDeleteDateIsNull(id: UUID): Optional<Terminal>

    fun findBySerialCodeAndTradeMarkAndModelAndDeleteDateIsNull(
        serialCode: String,
        tradeMark: String,
        model: String
    ): Optional<Terminal>

}
