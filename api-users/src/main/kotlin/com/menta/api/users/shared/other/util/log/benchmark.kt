package com.menta.api.users.shared.other.util.log

import org.slf4j.Logger
import org.slf4j.MDC

data class BenchmarkResult<T>(val response: T, val elapsedTime: Long)

/**
 * It receives a block, executes it and returns the result of the block with the elapsed time.
 * @param block
 * @return @see BenchmarkResult<T>
 */
inline fun <T> measureTimeMillisWithResponse(block: () -> T): BenchmarkResult<T> =
    System.currentTimeMillis().let { start ->
        block().let { response ->
            BenchmarkResult(response, System.currentTimeMillis() - start)
        }
    }

/**
 * Receives a block.
 * Logs the start with @param actionName and executes the block.
 * Logs the end and return block result.
 * @param logger
 * @param actionName
 * @param block
 */
inline fun <T> Logger.benchmark(actionName: String, debugLevel: Boolean = false, block: () -> T): T =
    also { info("Start execution of {}", actionName) }
        .let { measureTimeMillisWithResponse(block) }
        .also { info("End execution of {} - elapsed time {} ms", actionName, it.elapsedTime) }
        .also { MDC.put("elapsed_time", it.elapsedTime.toString()) }
        .response
