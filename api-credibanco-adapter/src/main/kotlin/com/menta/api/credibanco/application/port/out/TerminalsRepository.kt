package com.menta.api.credibanco.application.port.out

import arrow.core.Option

interface TerminalsRepository {
    fun register(terminalId: String): Option<String>
    fun exists(terminalId: String): Boolean
    fun deleteAll(): Boolean
}
