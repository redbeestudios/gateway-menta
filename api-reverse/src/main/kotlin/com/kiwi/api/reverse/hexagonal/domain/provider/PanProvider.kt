package com.kiwi.api.reverse.hexagonal.domain.provider

import com.kiwi.api.reverse.hexagonal.domain.Card
import org.springframework.stereotype.Component

@Component
class PanProvider(
    private val maskedPanProvider: MaskedPanProvider
) {

    fun provideFrom(card: Card) =
        with(card) {
            pan ?: getPanFromCardTrack2(track2!!)
        }

    fun provideMaskedFrom(card: Card) =
        provideFrom(card).mask()

    private fun getPanFromCardTrack2(track2: String) =
        track2.substringBefore("=")

    private fun String.mask() =
        maskedPanProvider.provide(this)

}
