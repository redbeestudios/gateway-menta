package com.menta.bff.devices.login.shared.adapter.out.model

import io.swagger.v3.oas.annotations.media.Schema
import org.springframework.format.annotation.NumberFormat

data class OrchestratedEntitiesRequest(
    @field:NumberFormat
    @Schema(type = "string", example = "50010531", description = "if present, terminal would be searched")
    val terminalSerialCode: String?,
    @Schema(type = "string", example = "I2000", description = "if present, device flows would be searched")
    val terminalModel: String?
)
