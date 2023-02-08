package com.kiwi.api.payments.adapter.controller

import com.kiwi.api.payments.application.port.`in`.PingInPort
import com.kiwi.api.payments.shared.error.providers.ErrorResponseProvider
import com.kiwi.api.payments.shared.util.log.CompanionLogger
import com.kiwi.api.payments.shared.util.log.benchmark
import com.kiwi.api.payments.shared.util.rest.evaluate
import org.springframework.http.HttpStatus.NO_CONTENT
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus

@Controller
@RequestMapping("/private/ping")
class PingController(
    private val pingInPort: PingInPort,
    private val errorResponseProvider: ErrorResponseProvider
) {
    @PostMapping
    @ResponseStatus(NO_CONTENT)
    fun ping() =
        log.benchmark("Ping gps Adapter") {
            execute()
                .evaluate(NO_CONTENT) { errorResponseProvider.provideFor(this) }
        }

    private fun execute() = pingInPort.execute()

    companion object : CompanionLogger()
}
