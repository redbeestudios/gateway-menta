package com.menta.api.feenicia.shared.util.log

import org.slf4j.Logger

fun Logger.exception(throwable: Throwable) =
    error("Exception: {} {} ", throwable.javaClass, throwable.stackTrace.joinToString { "-> $it" })
