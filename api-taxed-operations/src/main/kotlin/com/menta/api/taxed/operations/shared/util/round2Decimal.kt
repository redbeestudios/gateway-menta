package com.menta.api.taxed.operations.shared.util

import java.math.BigDecimal
import java.math.RoundingMode

fun BigDecimal.round2Decimal() = this.setScale(2, RoundingMode.HALF_UP)
