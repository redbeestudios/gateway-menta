package com.menta.api.users.authorities.shared.other.util

fun Throwable.getRootException(): Throwable =
    if (cause == null || this == cause)
        this
    else cause!!.getRootException()
