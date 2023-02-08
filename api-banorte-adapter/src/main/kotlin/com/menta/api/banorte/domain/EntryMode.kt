package com.menta.api.banorte.domain

enum class EntryMode(val code: String) {
    MANUAL("01"),
    MAG_STRIPE("02"),
    CHIP("05"),
    CONTACTLESS("07");
}
