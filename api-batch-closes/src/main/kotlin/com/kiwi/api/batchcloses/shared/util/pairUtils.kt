package com.kiwi.api.batchcloses.shared.util

fun <T, S> T.pairedWith(second: S) =
    this to second
