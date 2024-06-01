plugins {
    alias(libs.plugins.hateitorrateit.android.library)
    alias(libs.plugins.hateitorrateit.android.hilt)
}

android {
    namespace = "com.grappim.hateitorrateit.utils.androidimpl"
}

dependencies {
    implementation(projects.utils.androidApi)
    implementation(projects.utils.filesApi)
    implementation(projects.core.async)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)

    implementation(libs.timber)

    testImplementation(libs.robolectric)
}
