@file:Suppress("EnumEntryName")

package com.grappim.hateitorrateit

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.ApplicationProductFlavor
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.ProductFlavor

enum class FlavorDimensions {
    store
}

enum class AppFlavors(
    val dimensions: FlavorDimensions,
    val versionNameSuffix: String,
    val applicationIdSuffix: String? = null
) {
    gplay(FlavorDimensions.store, "-gplay"),
    fdroid(FlavorDimensions.store, "-fdroid", ".fdroid")
}

fun configureFlavors(
    commonExtension: CommonExtension<*, *, *, *, *, *>,
    flavorConfigurationBlock: ProductFlavor.(flavor: AppFlavors) -> Unit = {},
) {
    commonExtension.apply {
        FlavorDimensions.values().forEach { flavorDimension ->
            flavorDimensions += flavorDimension.name
        }

        productFlavors {
            AppFlavors.values().forEach { hiorFlavor ->
                register(hiorFlavor.name) {
                    dimension = hiorFlavor.dimensions.name
                    flavorConfigurationBlock(this, hiorFlavor)
                    if (this@apply is ApplicationExtension && this is ApplicationProductFlavor) {
                        versionNameSuffix = hiorFlavor.versionNameSuffix
                        if (hiorFlavor.applicationIdSuffix != null) {
                            applicationIdSuffix = hiorFlavor.applicationIdSuffix
                        }
                    }
                }
            }
        }
    }
}