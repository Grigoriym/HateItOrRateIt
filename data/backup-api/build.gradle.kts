plugins {
    alias(libs.plugins.hateitorrateit.android.library)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.grappim.hateitorrateit.data.backupapi"
}

dependencies {
    api(projects.data.repoApi)
    api(projects.data.localDatastorageApi)

    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.collections)
}
