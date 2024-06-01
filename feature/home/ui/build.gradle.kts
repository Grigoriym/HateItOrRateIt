plugins {
    alias(libs.plugins.hateitorrateit.android.library)
    alias(libs.plugins.hateitorrateit.android.library.compose)
    alias(libs.plugins.hateitorrateit.android.hilt)
}

android {
    namespace = "com.grappim.hateitorrateit.feature.home.ui"

    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
}

dependencies {
    implementation(projects.data.repoApi)
    implementation(projects.analyticsApi)
    implementation(projects.uikit)
    implementation(projects.commons)
    implementation(projects.utils.dateTimeApi)
    implementation(projects.utils.ui)
    implementation(projects.core.navigation)

    implementation(libs.androidx.lifecycle.runtime.core)
    implementation(libs.androidx.core.ktx)

    implementation(libs.kotlinx.coroutines.core)

    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.ui.core)
    implementation(libs.androidx.compose.material.core)
    implementation(libs.androidx.hilt.navigation.compose)

    implementation(libs.coil)

    testImplementation(libs.robolectric)
}
