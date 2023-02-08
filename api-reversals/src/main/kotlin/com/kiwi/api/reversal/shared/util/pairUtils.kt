package com.kiwi.api.reversal.shared.util

fun <T, S> T.pairedWith(second: S) =
    this to second
