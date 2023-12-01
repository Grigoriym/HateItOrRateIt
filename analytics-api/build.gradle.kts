plugins {
    alias(libs.plugins.hateitorrateit.android.library)
}

android {
    namespace = "com.grappim.hateitorrateit.analyticsapi"

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }
}
