package com.menta.api.credibanco.domain

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.UUID

data class CredibancoTerminal(
    @JsonProperty("terminal_id")
    val terminalId: UUID,
    @JsonProperty("logic_id")
    val logicId: String,
    @JsonProperty("unique_code")
    val uniqueCode: String
)
