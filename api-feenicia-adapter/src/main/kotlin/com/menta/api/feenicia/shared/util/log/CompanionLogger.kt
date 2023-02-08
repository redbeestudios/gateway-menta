package com.menta.api.feenicia.shared.util.log

import arrow.core.Either
import org.slf4j.Logger
import org.slf4j.LoggerFactory

abstract class CompanionLogger {

    @Suppress("JAVA_CLASS_ON_COMPANION")
    val log: Logger by lazy { LoggerFactory.getLogger(javaClass.enclosingClass) }

    inline fun <T> T.log(block: Logger.(T) -> Unit): T =
        also { block(log, this) }

    fun <L, R> Either<L, R>.logEither(left: Logger.(L) -> Unit, right: Logger.(R) -> Unit): Either<L, R> =
        also {
            fold({ log.left(it) }, { log.right(it) })
        }

    fun <L, R> Either<L, R>.logLeft(left: Logger.(L) -> Unit): Either<L, R> =
        also {
            fold({ log.left(it) }, {})
        }

    fun <L, R> Either<L, R>.logRight(right: Logger.(R) -> Unit): Either<L, R> =
        also {
            fold({}, { log.right(it) })
        }
}
