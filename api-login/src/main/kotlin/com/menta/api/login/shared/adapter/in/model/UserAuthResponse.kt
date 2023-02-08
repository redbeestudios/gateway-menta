package com.menta.api.login.shared.adapter.`in`.model

import com.menta.api.login.challenge.domain.ChallengeAttribute
import com.menta.api.login.challenge.domain.ChallengeName
import io.swagger.v3.oas.annotations.media.Schema

data class UserAuthResponse(
    @Schema(
        description = "Tokens provided by cognito, in case a challenge has been presented, no token would be provided"
    )
    val token: Token?,

    @Schema(description = "Challenge the user needs to pass to get a token, most commonly a new password required")
    val challenge: Challenge?
) {
    data class Token(
        @Schema(
            type = "string",
            example = "eyJraWQiOiJ6dU1makF0b21XNDYwVFN1Nm5ORkQrbXlUTFA3WHR0REQyVGgydHdVTnlrPSIsImFsZyI6IlJTMjU2In0.eyJzdWIiOiIxZmE0MDhhYi1lMzliLTQ3OTQtYWZhZC05OTUxYTMxMWYwZDAiLCJpc3MiOiJodHRwczpcL1wvY29nbml0by1pZHAudXMtZWFzdC0xLmFtYXpvbmF3cy5jb21cL3VzLWVhc3QtMV85cUtIZTE2UVkiLCJjbGllbnRfaWQiOiI0NDF1MTVnZG1yZjI2bDJyaDcyODdoM21mOCIsIm9yaWdpbl9qdGkiOiIxYWRkNTY3Yy0wMjc3LTQ4MzUtYTU2Ni0wYTBjYTFkZWQwMDEiLCJldmVudF9pZCI6IjEwZGU0MjMzLTdiMzgtNDU5YS1hODg1LTBjMDZhMGJlNzI2YiIsInRva2VuX3VzZSI6ImFjY2VzcyIsInNjb3BlIjoiYXdzLmNvZ25pdG8uc2lnbmluLnVzZXIuYWRtaW4iLCJhdXRoX3RpbWUiOjE2NTUzOTQzNTUsImV4cCI6MTY1NTM5Nzk1NSwiaWF0IjoxNjU1Mzk0MzU1LCJqdGkiOiJhZWNjYjFiYS03ZTY5LTQzMWEtOTJkMi0zY2RiNDdkZjFjY2MiLCJ1c2VybmFtZSI6InNlYmFzdGlhbi5tYXJ0aW5lekByZWRiLmVlIn0.YNjBlL6hXKHEeJeCZ2YQ5_wObkXv46zW0kWPB6o-obX-jvK4lOWzKGG9E39IDd00CcOuG2jLMFM0sL18XKGAPqcRg647xfrHztfCSJOLUWpgY3fIhBbTKWODlbkeXZGTHO2TKR9gob-qJ5zYGyVl2CYWWMpUSu8i4WBuH3O87qAs5WFtj_eCtgihglfuWw2ACIEjgdQj550WeQfgRESIx-n2PNV0Vi_5EJin2a66yAigrsgiGRcsjUq1tvaYgqBCIRFxPAcb3zOkA2Y9Qw1NRug-c3mhmwry7z7BteO25x1Gz8fTiEcgaQW3nWkPfbrlHPX88V7yrAL66VQlDeQCnA",
            description = "A valid access token that Amazon Cognito issued to the user who you want to authenticate."
        )
        val accessToken: String,

        @Schema(type = "int", example = "3600", description = "The expiration period of the authentication result in seconds.")
        val expiresIn: Int,

        @Schema(type = "string", example = "Bearer", description = "The token type.")
        val tokenType: String,

        @Schema(
            type = "string",
            example = "eyJjdHkiOiJKV1QiLCJlbmMiOiJBMjU2R0NNIiwiYWxnIjoiUlNBLU9BRVAifQ.AcaxxUTMCa9ZOpALwmgV_1UywGkPefBqUCNBg-EJUuPLrVj7xk49nLfhDQlistV7pWKBREQBXVNrC3bJiNBCKut6IYkFrOqMf7kT3ObEh2k7-0rd-iZIsUNJrjSEWd-NTIRbUqH60f6qVMQPkTFnY3cy1BuUCb0o3L0d-OHHbhIi7eYEbTyi1pMEmtmvVKfRBdXBCAmM3AuAiRGsovcvGpPhdb-fL-BHGEX6yTHMhW_hzSmxeivT19JKaSDHw6gqLN5IuKXwS0bQ417AGyGvtzqQqqNrRoIVp8jJD4uW6tTBbsS2VvD37_ETlP3-wOYCevNLMyRQRKmQxRpwVuA95A.GiYTBlQmqSpVE0zN.tX2U85V1ZEuM9ad1pa9t3z3BtQAq87_z8zT47ZLUuUy6wsEdUILLK8KiWR-zzO4jb2nrvZTvNgXq15TbyCxQM1URlMw3MVPmA1vu-sfX7cum5bl704j72yvO9R7Z53Hgtt0lcH8aD24kv6iKg8oVVY2NUJ9nT6HVudJytovB4X6FmvXSl1zSWGfyJtMALpL3hr7DaW5epRj4wowNCCGWcQ0UHt3gi3mZEqPWf1Ooosg2riEmXKtrtTyWIPCXzoQOPbhszrVoSpJlUUy_m75bV0KztUp7u-aYLvfxTww4g1FUeJ8h9PJ0-3I0NvgzPlDrkjywN6MofP1SO-MVm6b9uZa1diRlC_17pv7Rj_t9KSeqidoJf7JA5eD50rBqkTYhku6hCJHauqxpaFzH1hbvs5uSiCToMrw3wSFmzzl3-Wz_p--Zs7M4nss86KWnHm2cbpjl27_Z-ZrPvarMErChXB7cLAZifaWqqBr6fS2XMVi_ceJSw-s-kBWdXI3P-4ScG0nWucoISKRSAePvWvaNJRhj-pzPYN9Vit8pduCcNno4oJ41Um2as4f2MlRpIFoXkl6iaJmaZpe_UW7FvejQydk6C5wwP7WX9Dk94EDv-4MohaGKvBgGoWfFVBICW6lDAaqlpKIEW0FONLSZqO-rI4lqoMt9BgP_tgMH8WtFrmtabMPF3DwXXBEfnAhSLmW2qUWPnIpyEFY8WookbltxKTu8sUTxNMU8qVsJCeUcjqof31xO6NvJVCdHywpqejOHa2phF2tC_cP-m4qN577fkdbYvY1dq5VQY0f0pY-UM7rTR22zR9H-xrI8LIXGRRprVQ4wDNBUbXRdP-davjiqOrkz5xxU145vKWlcR9C6q6LiI5PQRhCXmAVP3Mbt328U3FN3PQ9YSAw_iduTqL-JSFHtn5gs7dh5C6SsdLFEL1RyzgPVeBjn0a8n63IzzzI2pyub0ep1lDmqvSrIp5himYBUYopbnoH05vmN8NIIXITYvj5OZc-_kkMaD1TTdz376ZQ4F9K5JExBqjoZnPeFzJOK-rWzTYaOPKchvRF62Itieo2bOIuwmaH357-tnQn4R-BknLqm21F-ZuWZbXHqk3Y3gheaOPB4ZJcc5GDUz8sxBbgTpzbq71oo0YeDw55rnK9HrGxcwc2gglslvvaX9saXM7bqWzdHfFQ29gsYNTRGmaTSsPtOxa4Xht6Jc9zyKvyQhujhaAAIdp6O0DEVnfSNmIwwEVQGazq1p32f5FhTW1grS7WFw_RSVFoXZ-dKiQ0WU_z5SlAo2-YoKjGZtne7e4YBEVqmlql9E0kx.Xs80BbP70sPB-weNaoCI8g",
            description = "The refresh token."
        )
        val refreshToken: String?,

        @Schema(
            type = "string",
            example = "eyJraWQiOiJMXC9mcHNuWTZGY3ZLQ2pDRGNMaE1xMmw1bzVlQWFLZDI1amtFSXEreEwxYz0iLCJhbGciOiJSUzI1NiJ9.eyJzdWIiOiIxZmE0MDhhYi1lMzliLTQ3OTQtYWZhZC05OTUxYTMxMWYwZDAiLCJlbWFpbF92ZXJpZmllZCI6ZmFsc2UsImlzcyI6Imh0dHBzOlwvXC9jb2duaXRvLWlkcC51cy1lYXN0LTEuYW1hem9uYXdzLmNvbVwvdXMtZWFzdC0xXzlxS0hlMTZRWSIsImNvZ25pdG86dXNlcm5hbWUiOiJzZWJhc3RpYW4ubWFydGluZXpAcmVkYi5lZSIsIm9yaWdpbl9qdGkiOiIxYWRkNTY3Yy0wMjc3LTQ4MzUtYTU2Ni0wYTBjYTFkZWQwMDEiLCJhdWQiOiI0NDF1MTVnZG1yZjI2bDJyaDcyODdoM21mOCIsImV2ZW50X2lkIjoiMTBkZTQyMzMtN2IzOC00NTlhLWE4ODUtMGMwNmEwYmU3MjZiIiwidG9rZW5fdXNlIjoiaWQiLCJhdXRoX3RpbWUiOjE2NTUzOTQzNTUsImV4cCI6MTY1NTM5Nzk1NSwiaWF0IjoxNjU1Mzk0MzU1LCJqdGkiOiI0MGJlYjlhOC0wNDExLTQyZDEtODA1NC01ODgzOTdkZWM5NGIiLCJlbWFpbCI6InNlYmFzdGlhbi5tYXJ0aW5lekByZWRiLmVlIn0.MTQkxn4ybL-n3f2rLN3j1H3xSfiNUpwlklEVJ9_NUNfAL0LIj4LN7IqZLChAZQuFlgKZ4fElSRQkYvhlm_uHkIIaSCr5LS2JxumBLcvizZtOmW2Hvw5K0OqXAMCq5QmPi12w1zBoZ409R0SkYzw1SLCNE88FQIOSgSPynEl_wCP3LIpuHyn0FhaoaZ8dbDF1wUxuH_Bex8cU4195VOPobcffirDvmx2gdzMi-3XFdCDdRYc4UePG9XqeQOjh6jAszSKDNkFa4310_IGMyzmfyD3TfPJ3YPzsdnU3-c8pmyQiosih1BgoyhNLAvJRG3DtxtvTTMEfS30SvxvtS8IcmA",
            description = "The ID token."
        )
        val idToken: String
    ) {
        override fun toString(): String {
            return "Token(expiresIn=$expiresIn, tokenType='$tokenType')"
        }
    }

    data class Challenge(
        @Schema(description = "Name of the challenge to solve")
        val name: ChallengeName,
        @Schema(description = "parameters that may help solving the challenge")
        val parameters: Map<ChallengeAttribute, String>,
        @Schema(description = "Session needed to solve the challenge")
        val session: String,
    ) {
        override fun toString(): String {
            return "Challenge(name=$name)"
        }
    }

}
