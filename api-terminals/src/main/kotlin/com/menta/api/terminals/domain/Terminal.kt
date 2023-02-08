package com.menta.api.terminals.domain

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.OffsetDateTime
import java.util.UUID

@Document(collection = "terminals")
data class Terminal(
    @Id
    val id: UUID,
    val merchantId: UUID,
    val customerId: UUID,
    @Indexed(name = "serial_code", unique = true)
    val serialCode: String,
    val hardwareVersion: String,
    val tradeMark: String,
    val model: String,
    val status: Status,
    val deleteDate: OffsetDateTime?,
    val features: List<Feature>
)
