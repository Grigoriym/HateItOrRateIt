plugins {
    alias(libs.plugins.hateitorrateit.android.library)
    alias(libs.plugins.hateitorrateit.android.hilt)
    alias(libs.plugins.hateitorrateit.kotlin.serialization)
}

android {
    namespace = "com.grappim.hateitorrateit.data.backupimpl"
}

dependencies {
    implementation(projects.data.backupApi)
    implementation(projects.data.repoApi)
    implementation(projects.data.localDatastorageApi)
    implementation(projects.utils.filesApi)
    implementation(projects.utils.dateTimeApi)
    implementation(projects.core.appinfoApi)
    implementation(projects.core.async)

    implementation(libs.androidx.core.ktx)
    implementation(libs.hilt.android)
    implementation(libs.timber)
}
