package com.kiwi.api.payments.config

import com.kiwi.api.payments.domain.field.provider.AggregatorProvider.Companion.log
import org.jpos.core.Configurable
import org.jpos.core.Configuration
import org.jpos.core.ConfigurationException
import org.jpos.util.LogEvent
import org.jpos.util.LogListener

class SLF4JListener : LogListener, Configurable {
    @Throws(ConfigurationException::class)
    override fun setConfiguration(cfg: Configuration?) {
    }

    override fun log(ev: LogEvent): LogEvent {
        if (ev.realm?.let { it.startsWith("channel/") } == true) {
            ev.log { info(it.toString()) }
        }
        return ev
    }
}
