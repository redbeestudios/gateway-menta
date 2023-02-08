package com.menta.api.login.shared.adapter.`in`.model.mapper

import com.menta.api.login.shared.adapter.`in`.model.UserAuthResponse
import com.menta.api.login.shared.domain.UserAuth
import com.menta.api.login.shared.other.util.log.CompanionLogger
import org.springframework.stereotype.Component

@Component
class ToUserAuthResponseMapper {

    fun mapFrom(userAuth: UserAuth): UserAuthResponse =
        with(userAuth) {
            UserAuthResponse(
                token = token?.let {
                    with(it) {
                        UserAuthResponse.Token(
                            accessToken = accessToken,
                            expiresIn = expiresIn,
                            tokenType = tokenType,
                            refreshToken = refreshToken,
                            idToken = idToken
                        )
                    }
                },
                challenge = challenge?.let {
                    with(it) {
                        UserAuthResponse.Challenge(
                            name = name,
                            parameters = parameters,
                            session = session
                        )
                    }
                }
            )
        }.log { info("response mapped: {}", it) }

    companion object : CompanionLogger()
}
