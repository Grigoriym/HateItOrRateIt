plugins {
    alias(libs.plugins.hateitorrateit.android.library)
    alias(libs.plugins.hateitorrateit.android.hilt)
}

android {
    namespace = "com.grappim.hateitorrateit.data.analyticsimpl"
}

dependencies {
    implementation(projects.data.analyticsApi)

    gplayImplementation(platform(libs.firebase.bom))
    gplayImplementation(libs.firebase.crashlytics)
    gplayImplementation(libs.firebase.analytics)

    implementation(libs.timber)
}
