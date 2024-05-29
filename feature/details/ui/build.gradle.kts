plugins {
    alias(libs.plugins.hateitorrateit.android.library)
    alias(libs.plugins.hateitorrateit.android.library.compose)
    alias(libs.plugins.hateitorrateit.android.hilt)
}

android {
    namespace = "com.grappim.hateitorrateit.feature.details.ui"
    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
}

dependencies {
    implementation(projects.uikit)
    implementation(projects.data.repoApi)
    implementation(projects.data.cleanerApi)
    implementation(projects.core.navigation)
    implementation(projects.analyticsApi)
    implementation(projects.utils.ui)
    implementation(projects.commons)
    implementation(projects.utils.dateTimeApi)
    implementation(projects.utils.filesApi)
    implementation(projects.utils.androidApi)

    implementation(libs.androidx.lifecycle.runtime.core)
    implementation(libs.androidx.lifecycle.runtime.compose)

    implementation(libs.kotlinx.coroutines.core)

    implementation(libs.androidx.compose.ui.core)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.material.core)
    implementation(libs.androidx.hilt.navigation.compose)

    implementation(libs.coil)
    implementation(libs.timber)

    implementation(libs.accompanist.permissions)

    testImplementation(libs.robolectric)
}
