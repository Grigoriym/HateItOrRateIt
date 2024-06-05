plugins {
    alias(libs.plugins.hateitorrateit.android.library)
    alias(libs.plugins.hateitorrateit.android.hilt)
}

android {
    namespace = "com.grappim.hateitorrateit.utils.filesimpl"
}

dependencies {
    implementation(projects.core.async)
    implementation(projects.data.repoApi)
    implementation(projects.utils.dateTimeApi)
    implementation(projects.utils.filesApi)

    implementation(libs.androidx.core.ktx)

    implementation(libs.timber)

    testImplementation(libs.robolectric)
}
