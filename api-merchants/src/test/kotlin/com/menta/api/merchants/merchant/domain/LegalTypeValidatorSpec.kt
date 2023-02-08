package com.menta.api.merchants.merchant.domain

import com.menta.api.merchants.aPreMerchant
import com.menta.api.merchants.domain.LegalType.LEGAL_ENTITY
import com.menta.api.merchants.domain.LegalType.NATURAL_PERSON
import com.menta.api.merchants.domain.LegalTypeValidator
import com.menta.api.merchants.shared.error.model.ApplicationError.Companion.invalidBusinessOwner
import com.menta.api.merchants.shared.error.model.ApplicationError.Companion.invalidLegalType
import com.menta.api.merchants.shared.error.model.ApplicationError.Companion.invalidRepresentative
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FeatureSpec

class LegalTypeValidatorSpec : FeatureSpec({

    val validator = LegalTypeValidator()

    feature("validate legal type for preMerchant") {

        scenario("natural person legalType but businessOwner doesnt exist") {
            val preMerchant = aPreMerchant().copy(legalType = NATURAL_PERSON, businessOwner = null)

            validator.validate(preMerchant) shouldBeLeft invalidBusinessOwner()
        }

        scenario("legal entity legalType but representative doesnt exist") {
            val preMerchant = aPreMerchant().copy(legalType = LEGAL_ENTITY, representative = null)

            validator.validate(preMerchant) shouldBeLeft invalidRepresentative()
        }

        scenario("natural person legalType and businessOwner exists") {
            val preMerchant = aPreMerchant().copy(legalType = NATURAL_PERSON, representative = null)

            validator.validate(preMerchant) shouldBeRight preMerchant
        }

        scenario("legal entity legalType but representative exists") {
            val preMerchant = aPreMerchant().copy(legalType = LEGAL_ENTITY, businessOwner = null)

            validator.validate(preMerchant) shouldBeRight preMerchant
        }

        scenario("used at the same time business_owner and representative ") {
            val preMerchant = aPreMerchant()

            validator.validate(preMerchant) shouldBeLeft  invalidLegalType()
        }
    }
})
