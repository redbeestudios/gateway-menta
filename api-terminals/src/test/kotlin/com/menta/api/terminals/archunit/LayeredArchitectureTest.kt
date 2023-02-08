package com.menta.api.terminals.archunit

import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.lang.ArchRule
import com.tngtech.archunit.library.Architectures.layeredArchitecture

private const val DOMAIN = "Domain"
private const val ADAPTERS = "Adapters"
private const val APPLICATION = "Application"
private const val CONFIG = "Config"

class LayeredArchitectureTest {

    @ArchTest
    val layer_dependencies_are_respected: ArchRule = layeredArchitecture()
        .layer(CONFIG).definedBy("com.menta.api.terminals.config..")
        .layer(DOMAIN).definedBy("com.menta.api.terminals.domain..")
        .layer(ADAPTERS).definedBy("com.menta.api.terminals.adapter..")
        .layer(APPLICATION).definedBy("com.menta.api.terminals.application..")
        .whereLayer(APPLICATION).mayOnlyBeAccessedByLayers(ADAPTERS, CONFIG)
        .whereLayer(ADAPTERS).mayOnlyBeAccessedByLayers(CONFIG)
        .whereLayer(DOMAIN).mayOnlyBeAccessedByLayers(APPLICATION, ADAPTERS, CONFIG)
}
