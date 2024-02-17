package com.grappim.hateitorrateit

/**
 * This is shared between :app and :benchmarks module to provide configurations type safety.
 */
enum class HateItOrRateItBuildTypes(val applicationIdSuffix: String? = null) {
    DEBUG(".debug"),
    RELEASE,
}
