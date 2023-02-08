package com.kiwi.api.payments.shared.error.providers

class KafkaConsumerException(message: String) :
    RuntimeException(message)
