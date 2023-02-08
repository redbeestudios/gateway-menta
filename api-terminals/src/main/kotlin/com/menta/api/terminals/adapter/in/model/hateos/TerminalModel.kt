package com.menta.api.terminals.adapter.`in`.model.hateos

import com.menta.api.terminals.domain.Feature
import com.menta.api.terminals.domain.Status
import org.springframework.hateoas.RepresentationModel
import java.time.OffsetDateTime
import java.util.UUID

data class TerminalModel(
    val id: UUID,
    val merchantId: UUID,
    val customerId: UUID,
    val serialCode: String,
    val hardwareVersion: String,
    val tradeMark: String,
    val model: String,
    val status: Status,
    val deleteDate: OffsetDateTime?,
    val features: List<Feature>
): RepresentationModel<TerminalModel>()
