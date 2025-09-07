plugins {
    alias(libs.plugins.hateitorrateit.android.library)
    alias(libs.plugins.hateitorrateit.android.library.compose)
    alias(libs.plugins.hateitorrateit.android.hilt)
    alias(libs.plugins.hateitorrateit.kotlin.serialization)
}

android {
    namespace = "com.grappim.hateitorrateit.feature.productmanager.ui"

    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
}

dependencies {
    implementation(projects.uikit)
    implementation(projects.data.repoApi)
    implementation(projects.data.localDatastorageApi)
    implementation(projects.data.cleanerApi)
    implementation(projects.utils.ui)
    implementation(projects.utils.filesApi)
    implementation(projects.data.analyticsApi)
    implementation(projects.strings)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.core)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.activity.compose)

    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.ui.core)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.hilt.navigation.compose)

    implementation(libs.kotlinx.coroutines.core)

    implementation(libs.timber)
    implementation(libs.coil)

    testImplementation(libs.robolectric)
}
