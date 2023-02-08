package com.menta.api.login.shared.domain

import com.menta.api.login.challenge.domain.ChallengeAttribute
import com.menta.api.login.challenge.domain.ChallengeName
import org.bouncycastle.asn1.cmp.Challenge

data class UserAuth(
    val token: Token?,
    val challenge: Challenge?
) {
    data class Token(
        val accessToken: String,
        val expiresIn: Int,
        val tokenType: String,
        val refreshToken: String?,
        val idToken: String
    ) {
        override fun toString(): String {
            return "Token(expiresIn=$expiresIn, tokenType='$tokenType')"
        }
    }

    data class Challenge(
        val name: ChallengeName,
        val parameters: Map<ChallengeAttribute, String>,
        val session: String,
    ) {
        override fun toString(): String {
            return "Challenge(name=$name)"
        }
    }
}
