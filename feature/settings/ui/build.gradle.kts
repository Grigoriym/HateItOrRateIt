plugins {
    alias(libs.plugins.hateitorrateit.android.library)
    alias(libs.plugins.hateitorrateit.android.library.compose)
    alias(libs.plugins.hateitorrateit.android.hilt)
    alias(libs.plugins.hateitorrateit.kotlin.serialization)
}

android {
    namespace = "com.grappim.hateitorrateit.feature.settings.ui"
}

dependencies {
    implementation(projects.utils.ui)
    implementation(projects.uikit)
    implementation(projects.data.analyticsApi)
    implementation(projects.core.async)
    implementation(projects.core.appinfoApi)
    implementation(projects.data.repoApi)
    implementation(projects.data.cleanerApi)
    implementation(projects.data.localDatastorageApi)
    implementation(projects.data.remoteConfigApi)

    implementation(libs.androidx.lifecycle.runtime.core)
    implementation(libs.androidx.lifecycle.runtime.compose)

    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.ktx)

    implementation(libs.androidx.hilt.navigation.compose)

    implementation(libs.androidx.compose.ui.core)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)

    implementation(libs.timber)

    testImplementation(libs.robolectric)
}
