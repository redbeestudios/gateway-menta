package com.menta.api.users.authorities.domain.provider

import com.menta.api.users.authorities.anUserAuthority
import com.menta.api.users.authorities.domain.UserType.MERCHANT
import com.menta.api.users.authorities.shared.other.authorities.UserAuthoritiesProperties
import com.menta.api.users.authorities.shared.other.error.model.ApplicationError.Companion.missingConfigurationForUserType
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FeatureSpec
import io.mockk.clearAllMocks

class AuthorityProviderSpec : FeatureSpec({

    beforeEach { clearAllMocks() }

    feature("authority provider by type") {
        val userType = MERCHANT
        val userAuthority = anUserAuthority

        scenario("with properties exist") {
            UserAuthoritiesProperties(
                provider = UserAuthoritiesProperties.Provider(
                    authorities = listOf(userAuthority)
                )
            ).let {
                AuthorityProvider(it).provideBy(userType) shouldBeRight userAuthority
            }
        }
        scenario("without properties") {
            UserAuthoritiesProperties(
                provider = UserAuthoritiesProperties.Provider(
                    authorities = emptyList()
                )
            ).let {
                AuthorityProvider(it).provideBy(userType) shouldBeLeft missingConfigurationForUserType(userType)
            }
        }
    }
})
