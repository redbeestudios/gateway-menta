package com.kiwi.api.reverse.shared.util

fun <T, S> T.pairedWith(second: S) =
    this to second
