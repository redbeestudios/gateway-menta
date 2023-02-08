package com.kiwi.api.payments.archunit

import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.lang.ArchRule
import com.tngtech.archunit.library.Architectures

private const val DOMAIN = "Domain"
private const val ADAPTERS = "Adapters"
private const val APPLICATION = "Application"
private const val CONFIG = "Config"

class LayeredArchitectureTest {

    @ArchTest
    val layer_dependencies_are_respected: ArchRule = Architectures.layeredArchitecture()
        .layer(CONFIG).definedBy("com.kiwi.api.payments.config..")
        .layer(DOMAIN).definedBy("com.kiwi.api.payments.domain..")
        .layer(ADAPTERS).definedBy("com.kiwi.api.payments.adapter..")
        .layer(APPLICATION).definedBy("com.kiwi.api.payments.application..")
        .whereLayer(APPLICATION).mayOnlyBeAccessedByLayers(ADAPTERS, CONFIG)
        .whereLayer(ADAPTERS).mayOnlyBeAccessedByLayers(CONFIG)
        .whereLayer(DOMAIN).mayOnlyBeAccessedByLayers(APPLICATION, ADAPTERS, CONFIG)
}
