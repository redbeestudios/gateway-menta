package com.menta.api.transactions.shared.util

fun <T, S> T.pairedWith(second: S) =
    this to second
