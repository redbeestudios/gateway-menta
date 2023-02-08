package com.menta.api.customers.shared.utils

fun Throwable.getRootException(): Throwable =
    if (cause == null || this == cause)
        this
    else cause!!.getRootException()
