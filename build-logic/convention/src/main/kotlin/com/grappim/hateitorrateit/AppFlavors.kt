package com.grappim.hateitorrateit

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.ApplicationProductFlavor
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.ProductFlavor

enum class FlavorDimensions {
    STORE
}

enum class AppFlavors(
    val title: String,
    val dimensions: FlavorDimensions,
    val applicationIdSuffix: String? = null
) {
    GPLAY("gplay", FlavorDimensions.STORE),
    FDROID("fdroid", FlavorDimensions.STORE, ".fdroid")
}

fun configureFlavors(
    commonExtension: CommonExtension<*, *, *, *, *, *>,
    flavorConfigurationBlock: ProductFlavor.(flavor: AppFlavors) -> Unit = {}
) {
    commonExtension.apply {

        FlavorDimensions.values().forEach { flavorDimension ->
            flavorDimensions += flavorDimension.name
        }

        productFlavors {
            AppFlavors.values().forEach { hiorFlavor ->
                register(hiorFlavor.title) {
                    dimension = hiorFlavor.dimensions.name
                    flavorConfigurationBlock(this, hiorFlavor)
                    if (this@apply is ApplicationExtension && this is ApplicationProductFlavor) {
                        if (hiorFlavor.applicationIdSuffix != null) {
                            applicationIdSuffix = hiorFlavor.applicationIdSuffix
                        }
                    }
                }
            }
        }
    }
}
