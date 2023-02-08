package com.kiwi.api.payments.shared.util.kafka

import arrow.core.Either
import com.kiwi.api.payments.shared.error.model.ApplicationError
import com.kiwi.api.payments.shared.error.providers.KafkaConsumerException

fun <R : Any> Either<ApplicationError, R>.evaluateResponse() =
    fold(
        ifLeft = { KafkaConsumerException(it.message) },
        ifRight = { it }
    )
