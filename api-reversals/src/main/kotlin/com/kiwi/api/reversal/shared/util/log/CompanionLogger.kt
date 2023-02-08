package com.kiwi.api.reversal.shared.util.log

import arrow.core.Either
import org.slf4j.Logger
import org.slf4j.LoggerFactory

abstract class CompanionLogger {

    @Suppress("JAVA_CLASS_ON_COMPANION")
    val log: Logger by lazy { LoggerFactory.getLogger(javaClass.enclosingClass) }

    inline fun <T> T.log(block: Logger.(T) -> Unit): T =
        also { block(log, this) }

    infix fun Logger.trace(message: String) = log.trace(message)
    infix fun Logger.info(message: String) = log.info(message)
    infix fun Logger.error(message: String) = log.error(message)

    fun <L, R> Either<L, R>.logEither(left: Logger.(L) -> Unit, right: Logger.(R) -> Unit): Either<L, R> =
        also {
            fold({ log.left(it) }, { log.right(it) })
        }

    fun <L, R> Either<L, R>.logRight(right: Logger.(R) -> Unit): Either<L, R> =
        also {
            fold({}, { log.right(it) })
        }
}
