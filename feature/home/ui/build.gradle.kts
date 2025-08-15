plugins {
    alias(libs.plugins.hateitorrateit.android.library)
    alias(libs.plugins.hateitorrateit.android.library.compose)
    alias(libs.plugins.hateitorrateit.android.hilt)
    alias(libs.plugins.hateitorrateit.kotlin.serialization)
}

android {
    namespace = "com.grappim.hateitorrateit.feature.home.ui"
}

dependencies {
    implementation(projects.data.repoApi)
    implementation(projects.data.analyticsApi)
    implementation(projects.uikit)
    implementation(projects.core.async)
    implementation(projects.utils.dateTimeApi)
    implementation(projects.utils.ui)

    implementation(libs.androidx.lifecycle.runtime.core)
    implementation(libs.androidx.core.ktx)

    implementation(libs.kotlinx.coroutines.core)

    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.ui.core)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.hilt.navigation.compose)

    implementation(libs.coil)

    testImplementation(libs.robolectric)
}
