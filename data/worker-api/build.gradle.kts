plugins {
    alias(libs.plugins.hateitorrateit.android.library)
}

android {
    namespace = "com.grappim.hateitorrateit.data.workerapi"

    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }
}
