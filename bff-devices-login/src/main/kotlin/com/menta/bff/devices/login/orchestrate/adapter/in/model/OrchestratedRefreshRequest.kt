package com.menta.bff.devices.login.orchestrate.adapter.`in`.model

import com.menta.bff.devices.login.shared.adapter.out.model.OrchestratedEntitiesRequest
import com.menta.bff.devices.login.shared.domain.Email
import com.menta.bff.devices.login.shared.domain.UserType
import io.swagger.v3.oas.annotations.media.Schema
import javax.validation.Valid

data class OrchestratedRefreshRequest(
    @Schema(
        type = "string",
        example = "eyJjdHkiOiJKV1QiLCJlbmMiOiJBMjU2R0NNIiwiYWxnIjoiUlNBLU9BRVAifQ.AcaxxUTMCa9ZOpALwmgV_1UywGkPefBqUCNBg-EJUuPLrVj7xk49nLfhDQlistV7pWKBREQBXVNrC3bJiNBCKut6IYkFrOqMf7kT3ObEh2k7-0rd-iZIsUNJrjSEWd-NTIRbUqH60f6qVMQPkTFnY3cy1BuUCb0o3L0d-OHHbhIi7eYEbTyi1pMEmtmvVKfRBdXBCAmM3AuAiRGsovcvGpPhdb-fL-BHGEX6yTHMhW_hzSmxeivT19JKaSDHw6gqLN5IuKXwS0bQ417AGyGvtzqQqqNrRoIVp8jJD4uW6tTBbsS2VvD37_ETlP3-wOYCevNLMyRQRKmQxRpwVuA95A.GiYTBlQmqSpVE0zN.tX2U85V1ZEuM9ad1pa9t3z3BtQAq87_z8zT47ZLUuUy6wsEdUILLK8KiWR-zzO4jb2nrvZTvNgXq15TbyCxQM1URlMw3MVPmA1vu-sfX7cum5bl704j72yvO9R7Z53Hgtt0lcH8aD24kv6iKg8oVVY2NUJ9nT6HVudJytovB4X6FmvXSl1zSWGfyJtMALpL3hr7DaW5epRj4wowNCCGWcQ0UHt3gi3mZEqPWf1Ooosg2riEmXKtrtTyWIPCXzoQOPbhszrVoSpJlUUy_m75bV0KztUp7u-aYLvfxTww4g1FUeJ8h9PJ0-3I0NvgzPlDrkjywN6MofP1SO-MVm6b9uZa1diRlC_17pv7Rj_t9KSeqidoJf7JA5eD50rBqkTYhku6hCJHauqxpaFzH1hbvs5uSiCToMrw3wSFmzzl3-Wz_p--Zs7M4nss86KWnHm2cbpjl27_Z-ZrPvarMErChXB7cLAZifaWqqBr6fS2XMVi_ceJSw-s-kBWdXI3P-4ScG0nWucoISKRSAePvWvaNJRhj-pzPYN9Vit8pduCcNno4oJ41Um2as4f2MlRpIFoXkl6iaJmaZpe_UW7FvejQydk6C5wwP7WX9Dk94EDv-4MohaGKvBgGoWfFVBICW6lDAaqlpKIEW0FONLSZqO-rI4lqoMt9BgP_tgMH8WtFrmtabMPF3DwXXBEfnAhSLmW2qUWPnIpyEFY8WookbltxKTu8sUTxNMU8qVsJCeUcjqof31xO6NvJVCdHywpqejOHa2phF2tC_cP-m4qN577fkdbYvY1dq5VQY0f0pY-UM7rTR22zR9H-xrI8LIXGRRprVQ4wDNBUbXRdP-davjiqOrkz5xxU145vKWlcR9C6q6LiI5PQRhCXmAVP3Mbt328U3FN3PQ9YSAw_iduTqL-JSFHtn5gs7dh5C6SsdLFEL1RyzgPVeBjn0a8n63IzzzI2pyub0ep1lDmqvSrIp5himYBUYopbnoH05vmN8NIIXITYvj5OZc-_kkMaD1TTdz376ZQ4F9K5JExBqjoZnPeFzJOK-rWzTYaOPKchvRF62Itieo2bOIuwmaH357-tnQn4R-BknLqm21F-ZuWZbXHqk3Y3gheaOPB4ZJcc5GDUz8sxBbgTpzbq71oo0YeDw55rnK9HrGxcwc2gglslvvaX9saXM7bqWzdHfFQ29gsYNTRGmaTSsPtOxa4Xht6Jc9zyKvyQhujhaAAIdp6O0DEVnfSNmIwwEVQGazq1p32f5FhTW1grS7WFw_RSVFoXZ-dKiQ0WU_z5SlAo2-YoKjGZtne7e4YBEVqmlql9E0kx.Xs80BbP70sPB-weNaoCI8g",
        description = "The refresh token."
    )
    val refreshToken: String,
    @Schema(type = "string", example = "mail@menta.global")
    val user: Email,
    @Schema(description = "Token owner user type's")
    val userType: UserType,
    @Valid
    @Schema(description = "parameters to search other entities")
    val orchestratedEntities: OrchestratedEntitiesRequest?
) {
    override fun toString(): String {
        return "RefreshRequest(user=$user, userType=$userType, orchestratedEntities=$orchestratedEntities)"
    }
}
