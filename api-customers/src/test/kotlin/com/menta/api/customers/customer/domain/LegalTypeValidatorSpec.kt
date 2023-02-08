package com.menta.api.customers.customer.domain

import com.menta.api.customers.aPreCustomer
import com.menta.api.customers.customer.domain.LegalType.LEGAL_ENTITY
import com.menta.api.customers.customer.domain.LegalType.NATURAL_PERSON
import com.menta.api.customers.shared.error.model.ApplicationError.Companion.invalidBusinessOwner
import com.menta.api.customers.shared.error.model.ApplicationError.Companion.invalidLegalType
import com.menta.api.customers.shared.error.model.ApplicationError.Companion.invalidRepresentative
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FeatureSpec

class LegalTypeValidatorSpec : FeatureSpec({

    val validator = LegalTypeValidator()

    feature("validate legal type for preCustomer") {

        scenario("natural person legalType but businessOwner doesnt exist") {
            val preCustomer = aPreCustomer.copy(legalType = NATURAL_PERSON, businessOwner = null)

            validator.validate(preCustomer) shouldBeLeft invalidBusinessOwner()
        }

        scenario("legal entity legalType but representative doesnt exist") {
            val preCustomer = aPreCustomer.copy(legalType = LEGAL_ENTITY, representative = null)

            validator.validate(preCustomer) shouldBeLeft invalidRepresentative()
        }

        scenario("natural person legalType and businessOwner exists") {
            val preCustomer = aPreCustomer.copy(legalType = NATURAL_PERSON, representative = null)

            validator.validate(preCustomer) shouldBeRight preCustomer
        }

        scenario("legal entity legalType but representative exists") {
            val preCustomer = aPreCustomer.copy(legalType = LEGAL_ENTITY, businessOwner = null)

            validator.validate(preCustomer) shouldBeRight preCustomer
        }

        scenario("used at the same time business_owner and representative ") {

            validator.validate(aPreCustomer) shouldBeLeft  invalidLegalType()
        }
    }
})
