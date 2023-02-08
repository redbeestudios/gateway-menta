package com.menta.api.merchants.shared.utils.logs

import org.slf4j.Logger

fun Logger.exception(throwable: Throwable) =
    error("Exception: {} {} ", throwable.javaClass, throwable.stackTrace.joinToString { "-> $it" })
