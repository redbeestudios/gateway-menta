package com.menta.api.credibanco.shared.redis

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.StringRedisTemplate

@Configuration
class RedisConfiguration {

    @Bean
    fun redisTemplate(connectionFactory: RedisConnectionFactory) =
        StringRedisTemplate()
            .apply { setConnectionFactory(connectionFactory) }
}
