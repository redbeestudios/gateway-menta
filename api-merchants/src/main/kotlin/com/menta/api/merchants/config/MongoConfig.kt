package com.menta.api.merchants.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.converter.Converter
import org.springframework.data.mongodb.config.EnableMongoAuditing
import org.springframework.data.mongodb.core.convert.MongoCustomConversions
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.Date

@Configuration
@EnableMongoAuditing
class MongoConfig {
    @Bean
    fun mongoCustomConversions(): MongoCustomConversions? {
        return MongoCustomConversions(
            listOf(
                OffsetDateTimeWriteConverter(),
                OffsetDateTimeReadConverter()
            )
        )
    }
}

internal class OffsetDateTimeWriteConverter :
    Converter<OffsetDateTime, Date> {
    override fun convert(source: OffsetDateTime): Date {
        return Date.from(source.toInstant().atZone(ZoneOffset.UTC).toInstant())
    }
}

internal class OffsetDateTimeReadConverter :
    Converter<Date, OffsetDateTime> {
    override fun convert(source: Date): OffsetDateTime {
        return source.toInstant().atOffset(ZoneOffset.UTC)
    }
}
