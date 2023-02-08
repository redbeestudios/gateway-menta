package com.menta.api.terminals.adapter.out

import arrow.core.right
import com.menta.api.terminals.aCustomerId
import com.menta.api.terminals.aSerialCode
import com.menta.api.terminals.aTerminal
import com.menta.api.terminals.aTerminalId
import com.menta.api.terminals.domain.Terminal
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.Optional
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query

class TerminalRepositorySpec : FeatureSpec({

    val dbRepository = mockk<TerminalDBRepository>()
    val mongoTemplate = mockk<MongoTemplate>(relaxed = true)
    val repository = TerminalRepository(dbRepository, mongoTemplate)

    beforeEach { clearAllMocks() }

    feature("find by terminal id") {

        scenario("terminal found") {

            every { dbRepository.findByIdAndDeleteDateIsNull(aTerminalId) } returns Optional.of(aTerminal)

            repository.findBy(aTerminalId) shouldBe Optional.of(aTerminal)

            verify(exactly = 1) { dbRepository.findByIdAndDeleteDateIsNull(aTerminalId) }
        }

        scenario("terminal NOT found") {

            every { dbRepository.findByIdAndDeleteDateIsNull(aTerminalId) } returns Optional.empty()

            repository.findBy(aTerminalId) shouldBe Optional.empty()

            verify(exactly = 1) { dbRepository.findByIdAndDeleteDateIsNull(aTerminalId) }
        }
    }

    feature("find by filter") {

        val pageable = PageRequest.of(0, 10)

        scenario("terminal found") {

            val query = Query(Criteria.where("customerId").`is`(aCustomerId))
            query.limit(pageable.pageSize)
            val count = 1L

            val result = PageImpl(listOf(aTerminal), pageable, count)

            every { mongoTemplate.count(any(), "terminals") } returns count
            every { mongoTemplate.find(any(), Terminal::class.java) } returns listOf(aTerminal)

            repository.findBy(null, null, aCustomerId, null,null, pageable) shouldBe result

            verify(exactly = 1) { mongoTemplate.count(any(), "terminals") }
            verify(exactly = 1) { mongoTemplate.find(any(), Terminal::class.java) }
        }

        scenario("terminal NOT found") {

            val query = Query(Criteria.where("customerId").`is`(aCustomerId))
            query.limit(pageable.pageSize)
            val count = 0L

            val result = PageImpl(emptyList<Terminal>(), pageable, count)

            every { mongoTemplate.count(any(), "terminals") } returns count
            every { mongoTemplate.find(any(), Terminal::class.java) } returns emptyList()

            repository.findBy(null, null, aCustomerId, null,null, pageable) shouldBe result

            verify(exactly = 1) { mongoTemplate.count(any(), "terminals") }
            verify(exactly = 1) { mongoTemplate.find(any(), Terminal::class.java) }
        }
    }

    feature("find by serial code") {

        scenario("terminal found") {

            every { dbRepository.findBySerialCode(aSerialCode) } returns Optional.of(aTerminal)

            repository.findBySerialCode(aSerialCode) shouldBe Optional.of(aTerminal)

            verify(exactly = 1) { dbRepository.findBySerialCode(aSerialCode) }
        }

        scenario("terminal NOT found") {

            every { dbRepository.findBySerialCode(aSerialCode) } returns Optional.empty()

            repository.findBySerialCode(aSerialCode) shouldBe Optional.empty()

            verify(exactly = 1) { dbRepository.findBySerialCode(aSerialCode) }
        }
    }

    feature("create") {

        scenario("terminal saved") {
            val newTerminal = aTerminal

            every { dbRepository.insert(newTerminal) } returns newTerminal

            repository.create(newTerminal) shouldBe newTerminal

            verify(exactly = 1) { dbRepository.insert(newTerminal) }
        }
    }

    feature("update") {
        scenario("terminal updated") {
            val terminal = aTerminal

            every { dbRepository.save(terminal) } returns terminal

            repository.update(terminal) shouldBe aTerminal.right()

            verify(exactly = 1) { dbRepository.save(terminal) }
        }
    }

    feature("find by serial code and trademark and model") {

        scenario("terminal found") {
            val terminal = aTerminal

            every {
                dbRepository.findBySerialCodeAndTradeMarkAndModelAndDeleteDateIsNull(
                    terminal.serialCode,
                    terminal.tradeMark,
                    terminal.model
                )
            } returns Optional.of(aTerminal)

            repository.findBy(terminal.serialCode, terminal.tradeMark, terminal.model) shouldBe Optional.of(aTerminal)

            verify(exactly = 1) {
                dbRepository.findBySerialCodeAndTradeMarkAndModelAndDeleteDateIsNull(
                    terminal.serialCode,
                    terminal.tradeMark,
                    terminal.model
                )
            }
        }

        scenario("terminal NOT found") {
            val terminal = aTerminal

            every {
                dbRepository.findBySerialCodeAndTradeMarkAndModelAndDeleteDateIsNull(
                    terminal.serialCode,
                    terminal.tradeMark,
                    terminal.model
                )
            } returns Optional.empty()

            repository.findBy(terminal.serialCode, terminal.tradeMark, terminal.model) shouldBe Optional.empty()

            verify(exactly = 1) {
                dbRepository.findBySerialCodeAndTradeMarkAndModelAndDeleteDateIsNull(
                    terminal.serialCode,
                    terminal.tradeMark,
                    terminal.model
                )
            }
        }
    }
})
