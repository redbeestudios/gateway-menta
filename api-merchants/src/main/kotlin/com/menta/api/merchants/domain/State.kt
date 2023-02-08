package com.menta.api.merchants.domain

import com.fasterxml.jackson.annotation.JsonCreator
import com.menta.api.merchants.domain.Country.ARG
import com.menta.api.merchants.domain.Country.MEX
import com.menta.api.merchants.shared.error.model.exception.InvalidCountryStateException

enum class State(val country: Country) {
    CAPITAL_FEDERAL(ARG),
    BUENOS_AIRES(ARG),
    CATAMARCA(ARG),
    CORDOBA(ARG),
    CORRIENTES(ARG),
    CHACO(ARG),
    CHUBUT(ARG),
    ENTRE_RIOS(ARG),
    FORMOSA(ARG),
    JUJUY(ARG),
    LA_PAMPA(ARG),
    LA_RIOJA(ARG),
    MENDOZA(ARG),
    MISIONES(ARG),
    NEUQUEN(ARG),
    RIO_NEGRO(ARG),
    SALTA(ARG),
    SAN_JUAN(ARG),
    SAN_LUIS(ARG),
    SANTA_FE(ARG),
    SANTA_CRUZ(ARG),
    SANTIAGO_DEL_ESTERO(ARG),
    TIERRA_DEL_FUEGO(ARG),
    TUCUMAN(ARG),
    AGUASCALIENTES(MEX),
    BAJA_CALIFORNIA(MEX),
    BAJA_CALIFORNIA_SUR(MEX),
    CAMPECHE(MEX),
    COAHUILA(MEX),
    COLIMA(MEX),
    CHIAPAS(MEX),
    CHIHUAHUA(MEX),
    DURANGO(MEX),
    DISTRITO_FEDERAL(MEX),
    GUANAJUATO(MEX),
    GUERRERO(MEX),
    HIDALGO(MEX),
    JALISCO(MEX),
    MEXICO(MEX),
    MICHOACAN(MEX),
    MORELOS(MEX),
    NAYARIT(MEX),
    NUEVO_LEON(MEX),
    OAXACA(MEX),
    PUEBLA(MEX),
    QUERETARO(MEX),
    QUINTANA_ROO(MEX),
    SAN_LUIS_POTOSI(MEX),
    SINALOA(MEX),
    SONORA(MEX),
    TABASCO(MEX),
    TAMAULIPAS(MEX),
    TLAXCALA(MEX),
    VERACRUZ(MEX),
    YUCATAN(MEX),
    ZACATECAS(MEX);

    companion object {
        @JvmStatic
        @JsonCreator
        fun forValue(value: String): State =
            State.values().find { it.name == value } ?: throw InvalidCountryStateException(value)
    }
}
