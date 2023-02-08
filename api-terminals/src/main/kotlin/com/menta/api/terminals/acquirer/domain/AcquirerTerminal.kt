package com.menta.api.terminals.acquirer.domain

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.CompoundIndex
import org.springframework.data.mongodb.core.mapping.Document
import java.util.UUID

@Document(collection = "acquirerTerminals")
@CompoundIndex(name = "acquirer_terminalId", def = "{'acquirer': 1, 'terminalId' : 1}", unique = true)
data class AcquirerTerminal(
    @Id
    val id: UUID,
    val terminalId: UUID,
    val acquirer: String,
    val code: String
)
