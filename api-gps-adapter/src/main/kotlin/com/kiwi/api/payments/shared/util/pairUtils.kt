package com.kiwi.api.payments.shared.util

fun <T, S> T.pairedWith(second: S) =
    this to second
