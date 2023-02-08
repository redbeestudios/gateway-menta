package com.menta.bff.devices.login.shared.other.util

fun Throwable.getRootException(): Throwable =
    if (cause == null || this == cause)
        this
    else cause!!.getRootException()
