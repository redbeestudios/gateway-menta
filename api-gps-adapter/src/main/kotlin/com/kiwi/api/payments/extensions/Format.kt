package com.kiwi.api.payments.extensions

import com.kiwi.api.payments.domain.PINCapabilities
import com.kiwi.api.payments.domain.field.InputMode

fun InputMode.toAcquirerFormat(): String =
    code + PINCapabilities.POSHasPinPad.code
