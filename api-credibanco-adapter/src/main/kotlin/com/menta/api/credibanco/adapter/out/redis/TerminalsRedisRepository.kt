package com.menta.api.credibanco.adapter.out.redis

import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import com.menta.api.credibanco.application.port.out.TerminalsRepository
import com.menta.api.credibanco.shared.util.log.CompanionLogger
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component

@Component
class TerminalsRedisRepository(
    @Value("\${spring.application.name}")
    private val application: String,
    private val template: StringRedisTemplate
) : TerminalsRepository {

    private val setKey = "$application:$TERMINALS_PREFIX"

    override fun register(terminalId: String): Option<String> =
        when (template.opsForSet().add(setKey, terminalId)) {
            0L -> None
            else -> Some(terminalId)
        }

    override fun exists(terminalId: String): Boolean =
        template
            .opsForSet()
            .isMember(setKey, terminalId)
            ?: false

    override fun deleteAll() =
        template.delete(setKey)

    companion object : CompanionLogger() {
        const val TERMINALS_PREFIX = "terminals"
    }
}
