package com.menta.api.users.shared.other.util

fun Throwable.getRootException(): Throwable =
    if (cause == null || this == cause)
        this
    else cause!!.getRootException()
