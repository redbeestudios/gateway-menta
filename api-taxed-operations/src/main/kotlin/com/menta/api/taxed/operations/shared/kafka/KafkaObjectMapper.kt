package com.menta.api.taxed.operations.shared.kafka

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonTypeRef

class KafkaObjectMapper(
    val objectMapper: ObjectMapper
) {

    inline fun <reified V> deserialize(value: String): V =
        objectMapper.readValue(value, jacksonTypeRef<V>())

    fun <V> serialize(value: V): String =
        objectMapper.writeValueAsString(value)
}
