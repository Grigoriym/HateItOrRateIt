plugins {
    alias(libs.plugins.hateitorrateit.android.library)
    alias(libs.plugins.hateitorrateit.android.hilt)
}

android {
    namespace = "com.grappim.hateitorrateit.data.localdatastorageimpl"

    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
}

dependencies {
    implementation(projects.data.repoApi)
    implementation(projects.data.localDatastorageApi)

    implementation(libs.androidx.datastore.prefs)
    implementation(libs.timber)
}
