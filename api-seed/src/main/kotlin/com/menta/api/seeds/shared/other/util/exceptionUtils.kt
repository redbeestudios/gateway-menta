package com.menta.api.seeds.shared.other.util

fun Throwable.getRootException(): Throwable =
    if (cause == null || this == cause)
        this
    else cause!!.getRootException()
