package com.menta.api.users.authorities.shared.other.util.log

import arrow.core.Either
import org.slf4j.Logger
import org.slf4j.LoggerFactory

abstract class CompanionLogger {

    @Suppress("JAVA_CLASS_ON_COMPANION")
    val log: Logger by lazy { LoggerFactory.getLogger(javaClass.enclosingClass) }

    inline fun <T> T.log(block: Logger.(T) -> Unit): T =
        also { block(log, this) }

    fun <L, R> Either<L, R>.logLeft(left: Logger.(L) -> Unit): Either<L, R> =
        also {
            fold({ log.left(it) }, {})
        }

    fun <L, R> Either<L, R>.logRight(right: Logger.(R) -> Unit): Either<L, R> =
        also {
            fold({}, { log.right(it) })
        }

    infix fun Logger.trace(message: String) = log.trace(message)
    infix fun Logger.debug(message: String) = log.debug(message)
    infix fun Logger.info(message: String) = log.info(message)
    infix fun Logger.warn(message: String) = log.warn(message)
    infix fun Logger.error(message: String) = log.error(message)
    infix fun Logger.exception(ex: Throwable) =
        error("Exception: {} {} ", ex.javaClass, ex.stackTrace.joinToString { "-> $it" })

    fun <L, R> Either<L, R>.logEither(left: Logger.(L) -> Unit, right: Logger.(R) -> Unit): Either<L, R> =
        also {
            fold({ log.left(it) }, { log.right(it) })
        }
}
