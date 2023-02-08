package com.kiwi.api.payments.domain

import com.fasterxml.jackson.annotation.JsonProperty

class AcquirerTerminal(
    @JsonProperty("terminal_id")
    val terminalId: String,
    @JsonProperty("acquirer")
    val acquirerId: String,
    val code: String
)
