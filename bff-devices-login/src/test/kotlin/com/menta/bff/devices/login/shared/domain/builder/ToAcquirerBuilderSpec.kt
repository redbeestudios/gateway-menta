package com.menta.bff.devices.login.shared.domain.builder

import com.menta.bff.devices.login.entities.acquirers.domain.AcquirerOperable
import com.menta.bff.devices.login.entities.installments.domain.AcquirerInstallment
import com.menta.bff.devices.login.entities.tags.domain.AcquirerTag
import com.menta.bff.devices.login.shared.domain.Acquirer
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks

class ToAcquirerMapperSpec : FeatureSpec({

    val builder = ToAcquirerBuilder()

    beforeEach { clearAllMocks() }

    feature("from Acquirer") {
        val acqOperable = AcquirerOperable(
            acquirerId = "GPS",
            code = "a code"
        )
        val acqInstallments = AcquirerInstallment(
            acquirerId = "GPS",
            installments = listOf(
                AcquirerInstallment.Installment(
                    brand = "VISA",
                    number = "3"
                )
            )
        )
        val acqEmvs = AcquirerTag(
            acquirerId = "GPS",
            type = "EMV",
            tags = listOf("9F26", "82", "9F36")
        )

        scenario("from acquirer and installments and tags") {
            builder.buildFrom(
                acquirers = listOf(acqOperable),
                installments = listOf(acqInstallments),
                tags = listOf(acqEmvs)
            ) shouldBe listOf(
                Acquirer(
                    acquirerId = "GPS",
                    code = "a code",
                    installments = listOf(
                        Acquirer.Installment(
                            brand = "VISA",
                            number = "3"
                        )
                    ),
                    tags = listOf("9F26", "82", "9F36")
                )
            )
        }
        scenario("from acquirer and different acquirer installments and tags") {
            builder.buildFrom(
                acquirers = listOf(acqOperable),
                installments = listOf(acqInstallments.copy(acquirerId = "FEENICIA")),
                tags = listOf(acqEmvs)
            ) shouldBe listOf(
                Acquirer(
                    acquirerId = "GPS",
                    code = "a code",
                    installments = null,
                    tags = listOf("9F26", "82", "9F36")
                )
            )
        }
        scenario("from acquirer and without acquirer installments and tags") {
            builder.buildFrom(
                acquirers = listOf(acqOperable),
                installments = null,
                tags = listOf(acqEmvs)
            ) shouldBe listOf(
                Acquirer(
                    acquirerId = "GPS",
                    code = "a code",
                    installments = null,
                    tags = listOf("9F26", "82", "9F36")
                )
            )
        }
        scenario("from acquirer installments and without acquirers and tags") {
            builder.buildFrom(
                acquirers = null,
                installments = listOf(acqInstallments),
                tags = listOf(acqEmvs)
            ) shouldBe null
        }
        scenario("from acquirer installments and empty acquirers") {
            builder.buildFrom(
                acquirers = listOf(),
                installments = listOf(acqInstallments),
                tags = listOf(acqEmvs)
            ) shouldBe null
        }
        scenario("from acquirer tags and without acquirers") {
            builder.buildFrom(
                acquirers = null,
                installments = null,
                tags = listOf(acqEmvs)
            ) shouldBe null
        }
        scenario("from acquirer tags and empty acquirers") {
            builder.buildFrom(
                acquirers = listOf(),
                installments = null,
                tags = listOf(acqEmvs)
            ) shouldBe null
        }
        scenario("from acquirers with empty acquirers installments") {
            builder.buildFrom(
                acquirers = listOf(acqOperable),
                installments = listOf(),
                tags = null
            ) shouldBe listOf(
                Acquirer(
                    acquirerId = "GPS",
                    code = "a code",
                    installments = null,
                    tags = null
                )
            )
        }
        scenario("from acquirers with empty acquirers tags") {
            builder.buildFrom(
                acquirers = listOf(acqOperable),
                installments = null,
                tags = listOf()
            ) shouldBe listOf(
                Acquirer(
                    acquirerId = "GPS",
                    code = "a code",
                    installments = null,
                    tags = null
                )
            )
        }
        scenario("from all params null") {
            builder.buildFrom(
                acquirers = null,
                installments = null,
                tags = null
            ) shouldBe null
        }
    }
})
