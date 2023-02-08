package com.menta.api.credibanco.config

import com.menta.api.credibanco.shared.util.log.CompanionLogger
import org.jpos.iso.ISOUtil
import org.jpos.iso.MUX
import org.jpos.q2.Q2
import org.jpos.q2.iso.QMUX
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class JposConfiguration(
    @Value("\${spring.profiles.active}")
    private val profile: String
) {
    @Bean
    fun q2() = Q2(arrayOf(COMMAND.plus(profile.lowercase()))).also {
        it.start()
    }

    @Bean
    fun mux(q2: Q2): MUX {
        while (!q2.ready()) {
            ISOUtil.sleep(100)
        }
        return QMUX.getMUX("jpos_muxpool")
    }

    companion object : CompanionLogger() {
        const val COMMAND = "-E"
    }
}
