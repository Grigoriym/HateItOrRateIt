plugins {
    alias(libs.plugins.hateitorrateit.android.library)
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

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.analytics)
}