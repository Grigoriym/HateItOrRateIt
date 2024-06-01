plugins {
    alias(libs.plugins.hateitorrateit.android.library)
    alias(libs.plugins.hateitorrateit.android.library.compose)
    alias(libs.plugins.hateitorrateit.android.hilt)
}

android {
    namespace = "com.grappim.hateitorrateit.feature.settings.ui"

    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
}

dependencies {
    implementation(projects.utils.ui)
    implementation(projects.uikit)
    implementation(project(":analytics-api"))
    implementation(project(":core:appinfo-api"))
    implementation(project(":data:cleaner-api"))
    implementation(project(":data:local-datastorage-api"))
    implementation(project(":data:remote-config-api"))
    implementation(projects.commons)
    implementation(projects.data.repoApi)
    implementation(projects.core.navigation)

    implementation(libs.androidx.lifecycle.runtime.core)
    implementation(libs.androidx.lifecycle.runtime.compose)

    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.ktx)

    implementation(libs.androidx.hilt.navigation.compose)

    implementation(libs.androidx.compose.ui.core)
    implementation(libs.androidx.compose.material.core)
    implementation(libs.androidx.compose.material.icons.extended)

    implementation(libs.timber)

    testImplementation(libs.robolectric)
}
