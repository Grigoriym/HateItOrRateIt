plugins {
    alias(libs.plugins.hateitorrateit.android.library)
    alias(libs.plugins.hateitorrateit.android.hilt)
}

android {
    namespace = "com.grappim.hateitorrateit.analyticsimpl"
}

dependencies {
    implementation(projects.analyticsApi)

    releaseImplementation(platform(libs.firebase.bom))
    releaseImplementation(libs.firebase.crashlytics)
    releaseImplementation(libs.firebase.analytics)

    debugImplementation(libs.timber)
}
