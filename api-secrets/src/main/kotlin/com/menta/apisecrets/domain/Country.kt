package com.menta.apisecrets.domain

import com.menta.apisecrets.shared.error.model.ApplicationError.Companion.invalidCountry
import com.menta.apisecrets.shared.util.getOrLeft

enum class Country {
    ARG,
    MEX;

    companion object {
        private val countryByDesc = values().associateBy { it.name }

        fun getBy(name: String) =
            countryByDesc.getOrLeft(name.uppercase(), invalidCountry(name))
    }
}
