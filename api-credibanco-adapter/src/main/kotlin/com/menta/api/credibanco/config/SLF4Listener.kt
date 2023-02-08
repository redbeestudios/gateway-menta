package com.menta.api.credibanco.config

import com.menta.api.credibanco.adapter.controller.PaymentController.Companion.log
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
