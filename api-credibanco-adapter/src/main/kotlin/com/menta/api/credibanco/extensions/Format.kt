package com.menta.api.credibanco.extensions

import com.menta.api.credibanco.domain.PINCapabilities
import com.menta.api.credibanco.domain.field.InputMode

fun InputMode.toAcquirerFormat(): String =
    code + PINCapabilities.POSHasPinPad.code
