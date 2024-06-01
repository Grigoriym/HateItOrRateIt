plugins {
    alias(libs.plugins.hateitorrateit.android.library)
    alias(libs.plugins.hateitorrateit.android.hilt)
}

android {
    namespace = "com.grappim.hateitorrateit.data.repoimpl"
}

dependencies {
    implementation(projects.utils.filesApi)
    implementation(projects.utils.dateTimeApi)
    implementation(projects.core.async)
    implementation(projects.data.repoApi)
    implementation(projects.data.db)
    implementation(projects.data.localDatastorageApi)

    implementation(libs.androidx.core.ktx)

    implementation(libs.androidx.sqlite)

    implementation(libs.timber)
}
