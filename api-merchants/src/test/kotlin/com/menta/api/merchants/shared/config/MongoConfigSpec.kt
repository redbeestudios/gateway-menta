package com.menta.api.merchants.shared.config

import com.menta.api.merchants.config.MongoConfig
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.types.shouldBeInstanceOf
import org.springframework.data.mongodb.core.convert.MongoCustomConversions

class MongoConfigSpec : FeatureSpec({

    val mongoConfig = MongoConfig()

    feature("find Custom Conversions") {
        scenario("conversions instance ok") {
            mongoConfig.mongoCustomConversions().shouldBeInstanceOf<MongoCustomConversions>()
        }
        scenario("conversions founds") {
            mongoConfig.mongoCustomConversions().shouldNotBeNull()
        }
    }
})
