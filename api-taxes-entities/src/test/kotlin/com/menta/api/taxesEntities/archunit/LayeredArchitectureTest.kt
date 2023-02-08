package com.menta.api.taxesEntities.archunit

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
        .layer(CONFIG).definedBy("com.menta.api.taxesEntities.config..")
        .layer(DOMAIN).definedBy("com.menta.api.taxesEntities.domain..")
        .layer(ADAPTERS).definedBy("com.menta.api.taxesEntities.adapter..")
        .layer(APPLICATION).definedBy("com.menta.api.taxesEntities.application..")
        .whereLayer(APPLICATION).mayOnlyBeAccessedByLayers(ADAPTERS, CONFIG)
        .whereLayer(ADAPTERS).mayOnlyBeAccessedByLayers(CONFIG)
        .whereLayer(DOMAIN).mayOnlyBeAccessedByLayers(APPLICATION, ADAPTERS, CONFIG)
}
