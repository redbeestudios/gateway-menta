package com.menta.api.credibanco.adapter.out.redis

import arrow.core.None
import com.menta.api.credibanco.aTerminalId
import io.kotest.assertions.arrow.core.shouldBeSome
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import org.springframework.data.redis.core.SetOperations
import org.springframework.data.redis.core.StringRedisTemplate

class TerminalsRedisRepositorySpec : FeatureSpec({

    val application = "api-credibanco-adapter"
    val template = mockk<StringRedisTemplate>()
    val repository = TerminalsRedisRepository(application, template)

    val setOperation = mockk<SetOperations<String, String>>()

    val terminalsSetKey = "$application:${TerminalsRedisRepository.TERMINALS_PREFIX}"

    beforeEach {
        clearAllMocks()
        every { template.opsForSet() } returns setOperation
    }

    feature("register") {

        scenario("successful registration") {

            every { setOperation.add(terminalsSetKey, aTerminalId) } returns 1

            repository.register(aTerminalId) shouldBeSome aTerminalId
        }

        scenario("unsuccessful registration") {

            every { setOperation.add(terminalsSetKey, aTerminalId) } returns 0

            repository.register(aTerminalId) shouldBe None
        }
    }

    feature("exists") {

        scenario("successful find") {

            every { setOperation.isMember(terminalsSetKey, aTerminalId) } returns true

            repository.exists(aTerminalId) shouldBe true
        }

        scenario("unsuccessful find") {

            every { setOperation.isMember(terminalsSetKey, aTerminalId) } returns false

            repository.exists(aTerminalId) shouldBe false
        }
    }

    feature("deleteAll") {

        scenario("successful key deletion") {

            every { template.delete(terminalsSetKey) } returns true

            repository.deleteAll() shouldBe true
        }

        scenario("key not found") {

            every { template.delete(terminalsSetKey) } returns false

            repository.deleteAll() shouldBe false
        }
    }
})
