plugins {
    alias(libs.plugins.hateitorrateit.android.library)
    alias(libs.plugins.hateitorrateit.android.hilt)
}

android {
    namespace = "com.grappim.hateitorrateit.analyticsimpl"

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }
}

dependencies {
    implementation(project(":analytics-api"))
    implementation(project(":domain"))

    releaseImplementation(platform(libs.firebase.bom))
    releaseImplementation(libs.firebase.crashlytics)
    releaseImplementation(libs.firebase.analytics)

    debugImplementation(libs.timber)
}
