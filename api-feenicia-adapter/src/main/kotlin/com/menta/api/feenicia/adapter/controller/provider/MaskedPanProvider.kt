package com.menta.api.feenicia.adapter.controller.provider

import org.springframework.stereotype.Component

@Component
class MaskedPanProvider {

    fun provide(pan: String?) =
        pan?.substring(0, pan.length - 4)?.replace("\\d".toRegex(), "X")
            ?.plus(pan.substring(pan.length - 4, pan.length))
}
