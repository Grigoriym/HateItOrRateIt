plugins {
    alias(libs.plugins.hateitorrateit.android.library)
    alias(libs.plugins.hateitorrateit.android.library.compose)
}

android {
    namespace = "com.grappim.hateitorrateit.utils.ui"

    testOptions {
        unitTests {
            isReturnDefaultValues = true
            isIncludeAndroidResources = true
        }
    }
}

dependencies {
    implementation(projects.core.async)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)

    implementation(libs.androidx.compose.material.icons.core)
    implementation(libs.androidx.compose.material.icons.extended)

    implementation(libs.androidx.navigation.compose)

    testImplementation(libs.robolectric)
}
